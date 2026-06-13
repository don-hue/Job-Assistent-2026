package org.ProjectX.controller;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.ProjectX.Database.SearchUrlRepository;
import org.ProjectX.util.Utils;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class UrlSearchDialogController {
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
                SearchUrlRepository db = SearchUrlRepository.getInstance();
                Utils utils = Utils.getInstance();
                if(userUrl.getText().isEmpty()){
                    String url  = buildUrl();
                    db.saveSearch(url);
                }

                if(!userUrl.getText().isEmpty() && utils.isValidURL(userUrl.getText())) {
                    db.saveSearch(userUrl.getText());
                }

                return null;
            }
        };

        task.setOnSucceeded( _ -> {
            showAlert("Erfolreich", "Die Suche wurde gespeichert !");
        });

        task.setOnFailed(_ -> {
            showAlert("Fehler", "Es gab einen Fehler. Bitte probiere es später nochmal.");
        });

        new Thread(task).start();
    }

    public void cancelUrl(){
        keywordField.clear();
        locationField.clear();

        portalBox.getSelectionModel().selectFirst();
        radiusBox.getSelectionModel().selectFirst();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
