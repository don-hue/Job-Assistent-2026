package org.ProjectX.controller;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.ProjectX.database.JobRepository;
import org.ProjectX.database.SearchUrlRepository;
import org.ProjectX.config.Constants;
import org.ProjectX.util.Utils;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class UrlSearchDialogController {

    Utils utils = Utils.getInstance();
    SearchUrlRepository searchDB = SearchUrlRepository.getInstance();
    JobRepository jobDB = JobRepository.getInstance();

    @FXML
    private HBox buttonHbox;
    @FXML
    private VBox urlVbox;
    @FXML
    private ComboBox<String> portalBox;

    @FXML
    private TextField keywordField;

    @FXML
    private TextField locationField;

    @FXML
    private TextArea userUrl;

    @FXML
    private ComboBox<String> radiusBox;

    @FXML
    public void initialize() {

        portalBox.getItems().addAll(
                "Stepstone"
        );

        radiusBox.getItems().addAll(
                "5km",
                "10km",
                "15km"
        );

        portalBox.getSelectionModel().selectFirst();
        radiusBox.getSelectionModel().selectFirst();
    }

    public String buildUrl() {
        String portal = portalBox.getValue();
        String keyword = keywordField.getText();
        String location = locationField.getText();
        String radius = radiusBox.getValue();
        return "https://www." + portal + ".de/" +
                "jobs/" + URLEncoder.encode(keyword, StandardCharsets.UTF_8).replace("+", "%20") + "/" +
                "in-" + location + "?whatType=autosuggest&" +
                "radius=" + radius + "&" +
                "q" + URLEncoder.encode(keyword, StandardCharsets.UTF_8).replace("+", "%20") + "&" +
                "searchOrigin=Resultlist_top-search";
    }

    public void saveUrl() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                double[] coordinates = utils.getGeoData(locationField.getText());
                Utils utils = Utils.getInstance();
                if(userUrl.getText().isEmpty()){
                    String url  = buildUrl();
                    searchDB.saveSearch(url,keywordField.getText(), portalBox.getValue(),locationField.getText(),radiusBox.getValue(), false);
                }

                if(!userUrl.getText().isEmpty() && utils.isValidURL(userUrl.getText())) {
                    searchDB.saveSearch(userUrl.getText(), "Custom URL used", "Custom", "Custom", "Custom",true);
                }

                String url = buildCommerzbankApiUrlNoGeo(
                        keywordField.getText(),
                        50,
                        "10",
                        "12",
                        locationField.getText(),
                        coordinates[0],
                        coordinates[1]
                );
                searchDB.saveSearch(Constants.FinanzInformatik_Jobpage, keywordField.getText(), "Finanz Informatik","Custom","Custom",false);
                searchDB.saveCommerzBankSearch(url, keywordField.getText());
                return null;
            }
        };

        task.setOnSucceeded( _ -> showAlert("Erfolgreich", "Die Suche wurde gespeichert !"));

        task.setOnFailed(_ -> showAlert("Fehler", "Es gab einen Fehler. Bitte probiere es später nochmal."));

        new Thread(task).start();
    }

    public void cancelUrl(){
        keywordField.clear();
        locationField.clear();

        portalBox.getSelectionModel().selectFirst();
        radiusBox.getSelectionModel().selectFirst();
    }

    public String buildCommerzbankApiUrlNoGeo(
            String keyword,
            int distance,
            String jobCategoryCode,
            String channelCode,
            String postalCode,
            double latitude,
            double longitude
    ) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            ObjectNode root = mapper.createObjectNode();
            root.put("LanguageCode", "DE");

            ObjectNode sp = root.putObject("SearchParameters");
            sp.put("FirstItem", 1);
            sp.put("CountItem", 10000);

            ArrayNode sort = sp.putArray("Sort");
            ObjectNode sortObj = mapper.createObjectNode();
            sortObj.put("Criterion", "PublicationStartDate");
            sortObj.put("Direction", "DESC");
            sort.add(sortObj);

            ArrayNode fields = sp.putArray("MatchedObjectDescriptor");
            fields.add("ID");
            fields.add("PositionTitle");
            fields.add("PositionURI");
            fields.add("PositionLocation.CityName");


            ArrayNode criteria = root.putArray("SearchCriteria");

            utils.add(criteria, mapper, "PositionFormattedDescription.Content", keyword);
            utils.add(criteria, mapper, "JobCategory.Code", jobCategoryCode);
            utils.add(criteria, mapper, "PublicationChannel.Code", channelCode);
            utils.add(criteria, mapper, "PositionLocation.Distance", String.valueOf(distance));
            utils.add(criteria, mapper, "PositionLocation.PostalCode", String.valueOf(postalCode));
            utils.add(criteria, mapper,
                    "PositionLocation.Latitude",
                    String.valueOf(latitude));

            // longitude
            utils.add(criteria, mapper,
                    "PositionLocation.Longitude",
                    String.valueOf(longitude));

            String json = mapper.writeValueAsString(root);

            return Constants.COMMERZBANK_API
                    + URLEncoder.encode(json, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void editSearch(String portal, String keyword, String postalCode, String radius, boolean isCustom) {
        portalBox.getItems().removeAll();
        portalBox.getItems().add(portal);
        portalBox.setDisable(true);
        portalBox.setValue(portal);
        keywordField.setText(keyword);
        userUrl.setDisable(true);
        buttonHbox.setVisible(false);

        if(!isCustom) {
            urlVbox.setVisible(false);
        }

        if(portal.toLowerCase().contains("stepstone")) {
            locationField.setText(postalCode);
            radiusBox.setValue(radius);
        } else {
            locationField.setText("");
            locationField.setDisable(true);
            radiusBox.setValue("");
            radiusBox.setDisable(true);
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public void updateSearch(Long id, Runnable updateSearchList, Runnable updateJobList){

        Task<Void> task = new Task<>(){
            @Override
            protected Void call(){
                searchDB.updateSearch(keywordField.getText(),locationField.getText(),radiusBox.getValue(), id);
                jobDB.deleteJobsBySearchId(id);
                return null;
            };
        };
        task.setOnSucceeded( _ ->{
            updateSearchList.run();
            updateJobList.run();
        } );

        task.setOnFailed(_ -> showAlert("Fehler", "Es gab einen Fehler. Bitte probiere es später nochmal."));

        new Thread(task).start();

    };

    public void test(){
        System.out.println("XXX keyword" + keywordField.getText());
        System.out.println("XXX location" + locationField.getText());
        System.out.println("XXX radius" + radiusBox.getValue());
    }
}
