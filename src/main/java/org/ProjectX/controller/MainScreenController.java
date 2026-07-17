package org.ProjectX.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.ProjectX.database.JobRepository;
import org.ProjectX.database.SearchUrlRepository;
import org.ProjectX.config.Constants;
import org.ProjectX.entity.JobEntity;
import org.ProjectX.entity.SearchUrlEntity;
import org.ProjectX.factories.Alert.AlertFactory;
import org.ProjectX.factories.Alert.AlertInterface;
import org.ProjectX.factories.Alert.InformationAlertFactory;
import org.ProjectX.factories.Alert.SearchDeletionAlertFactory;
import org.ProjectX.factories.Checkbox.AppliedCheckboxFactory;
import org.ProjectX.factories.Checkbox.BanCheckboxFactory;
import org.ProjectX.factories.Checkbox.CheckboxFactory;
import org.ProjectX.factories.Checkbox.CheckboxInterface;
import org.ProjectX.factories.Crawler.CrawlerFactory;
import org.ProjectX.factories.Crawler.CrawlerInterface;
import org.ProjectX.factories.Crawler.FinanzInformatik;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Optional;

public class MainScreenController {
    @FXML
    private VBox jobContainer;
    @FXML
    private VBox searchCardContainer;
    @FXML
    private TextFlow textFlow;
    @FXML
    private ScrollPane jobScrollPane;
    @FXML
    private ScrollPane searchScrollPane;
    @FXML
            private TabPane tabPane;
    Dialog<Void> dialog = new Dialog<>();
    AlertFactory informationFactory = new InformationAlertFactory();
    AlertInterface alertInformation = informationFactory.createAlert();
    AlertFactory searchDeletionFactory = new SearchDeletionAlertFactory();
    AlertInterface alertSearchDeletion = searchDeletionFactory.createAlert();
    CheckboxFactory checkboxBanFactory = new BanCheckboxFactory();
    CheckboxInterface checkboxBanInterface = checkboxBanFactory.create();
    CheckboxFactory checkboxAppliedFactory = new AppliedCheckboxFactory();
    CheckboxInterface checkboxAppliedInterface = checkboxAppliedFactory.create();

    @FXML
    public void initialize() {
        clipJobScrollPane();
        clipSearchScrollPane();
        tutorialText();

        Platform.runLater(() -> {

            Node header = tabPane.lookup(".tab-header-area");
            Node headerBg = tabPane.lookup(".tab-header-background");

            if (header != null) {
                header.setStyle("-fx-background-color: #2FA084;");
            }

            if (headerBg != null) {
                headerBg.setStyle("-fx-background-color: #2FA084;");
            }
        });


    }

    private HBox createJobCard(String jobTitle, String company, URL url, boolean applied) {
        HBox card = new HBox(15);
        VBox content = createLabelsForCard(jobTitle, company, url);
        HBox checkboxContainer = checkboxBanInterface.createSpecificCheckbox(company,jobTitle, applied, this::taskLoadJob);
        HBox checkboxContainer2 = checkboxAppliedInterface.createSpecificCheckbox(company,jobTitle,applied, this::taskLoadJob);
        Region spacer = new Region();
        String bgColor = applied
                ? "lightgray"
                : "white";
        card.setStyle(
                        "-fx-background-color:"  + bgColor + ";" +
                        "-fx-padding: 15;" +
                        "-fx-background-radius: 10;"

        );
        card.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(spacer, Priority.ALWAYS);
        card.getChildren().addAll(content, spacer,checkboxContainer, checkboxContainer2);

        return card;
    }
    private HBox createSearchCard(String portal, String keyword, String url, String postalCode, String radius, boolean isCustom, Long id) {
        HBox card = new HBox(15);
        VBox buttonContent = createButtonsForCard(portal, keyword,url,postalCode,radius,isCustom, id);
        URL realURL;
        try {
            realURL = URI.create(url).toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        VBox content = createLabelsForCard(keyword, portal, realURL);
        Region spacer = new Region();
        card.setStyle(
                "-fx-background-color:white;" +
                        "-fx-padding: 15;" +
                        "-fx-background-radius: 10;"
        );
        card.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(spacer, Priority.ALWAYS);
        card.getChildren().addAll(content, spacer, buttonContent);
        return card;
    }
    private Button createEditButton(String portal, String keyword, String url, String postalCode, String radius, boolean isCustom, Long id){
        Button button = new Button("Bearbeiten");
        button.setPrefWidth(80);
        button.setPrefHeight(20);
        button.setOnAction(event -> {
            try {
                openEditSearchConfigDialog(url,portal, keyword,postalCode,radius,isCustom,id);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
            return button;
    }
    private Button createDeleteButton(String url ){
        Button button = new Button("Löschen");
        button.setStyle("-fx-background-color: #ffb3b3;");
        button.setPrefWidth(80);
        button.setPrefHeight(20);
        button.setOnAction(event -> {
               alertSearchDeletion.showAlertDeleteSearch(Constants.DELETE_SEARCH_TITLE, Constants.DELETE_SEARCH_HEADER, Constants.DELETE_SEARCH_TEXT, url, this::taskLoadSearch);
        });
        return button;
    };
    private VBox createLabelsForCard(String jobTitle, String company, URL url) {
        VBox content = new VBox(5);
        Label title = new Label(jobTitle);
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Hyperlink link = new Hyperlink(company);
        link.setOnAction(_ -> {
            try {
                Desktop.getDesktop().browse(
                       url.toURI()
                );
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        content.getChildren().addAll(title, link);
        return content;
    }
    private VBox createButtonsForCard(String portal, String keyword, String url, String postalCode, String radius, boolean isCustom,Long id) {
        VBox content = new VBox(5);
        Button editButton = createEditButton(portal, keyword, url, postalCode, radius,isCustom,id);
        Button removeButton = createDeleteButton(url);

        content.getChildren().addAll(editButton, removeButton);
        return content;
    }
    private void clipJobScrollPane() {
        Rectangle clip = new Rectangle();

        clip.widthProperty().bind(jobScrollPane.widthProperty());
        clip.heightProperty().bind(jobScrollPane.heightProperty());

        clip.setArcWidth(42);
        clip.setArcHeight(42);

        jobScrollPane.setClip(clip);
    }
    private void clipSearchScrollPane() {
        Rectangle clip = new Rectangle();

        clip.widthProperty().bind(searchScrollPane.widthProperty());
        clip.heightProperty().bind(searchScrollPane.heightProperty());

        clip.setArcWidth(42);
        clip.setArcHeight(42);

        searchScrollPane.setClip(clip);
    }
    private void tutorialText() {
        textFlow.setLineSpacing(5);
        Text header = new Text("Anleitung - So geht's:\n");
        header.setStyle("-fx-font-weight: bold; -fx-font-size: 20px;");

        Text desc1 = new Text("1. Klicke auf den Button: Suche anlegen\n");
        desc1.setStyle("-fx-font-size: 16px;");

        Text desc2 = new Text("2. Gebe deine Parameter ein\n");
        desc2.setStyle("-fx-font-size: 16px;");

        Text desc3 = new Text("3. Speichere deine Suche. Mehrere Suchen sind auch möglich\n");
        desc3.setStyle("-fx-font-size: 16px;");

        Text desc4 = new Text("6. Starte eine Suche\n");
        desc4.setStyle("-fx-font-size: 16px;");

        Text desc5 = new Text("7. Drück auf den Button: Jobs anzeigen\n");
        desc5.setStyle("-fx-font-size: 16px;");


        textFlow.getChildren().addAll(header, desc1, desc2,desc3, desc4, desc5);
    }
    private void createLoadingDialogUrl(){

        dialog.setTitle("Bitte warten");
        dialog.setHeaderText("Die Suche wird konfiguriert...");

        Node header = dialog.getDialogPane().lookup(".header-panel");

        if (header != null) {
            header.setStyle(
                    "-fx-background-color: white;" +
                            "-fx-padding: 15;"
            );
        }

        ProgressIndicator progress = new ProgressIndicator();
        progress.setStyle(
                "-fx-progress-color: #3B82F6;"
        );
        dialog.getDialogPane().setContent(progress);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        dialog.getDialogPane().setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-radius: 16;" +
                        "-fx-border-color: #E5E7EB;" +
                        "-fx-border-width: 1;" +
                        "-fx-padding: 20;"
        );

        dialog.show();

    }
    private void createLoadingDialogJobs(){
        dialog.setTitle("Bitte warten");
        dialog.setHeaderText("Jobs werden gesucht...");

        Node header = dialog.getDialogPane().lookup(".header-panel");

        if (header != null) {
            header.setStyle(
                    "-fx-background-color: white;" +
                            "-fx-padding: 15;"
            );
        }

        ProgressIndicator progress = new ProgressIndicator();
        progress.setStyle(
                "-fx-progress-color: #3B82F6;"
        );
        dialog.getDialogPane().setContent(progress);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        dialog.getDialogPane().setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-radius: 16;" +
                        "-fx-border-color: #E5E7EB;" +
                        "-fx-border-width: 1;" +
                        "-fx-padding: 20;"
        );

        dialog.show();
    }
    private void createLoadingDialogSearch(){
        dialog.setTitle("Bitte warten");
        dialog.setHeaderText("Suchaufträge werden gesucht...");

        Node header = dialog.getDialogPane().lookup(".header-panel");

        if (header != null) {
            header.setStyle(
                    "-fx-background-color: white;" +
                            "-fx-padding: 15;"
            );
        }

        ProgressIndicator progress = new ProgressIndicator();
        progress.setStyle(
                "-fx-progress-color: #3B82F6;"
        );
        dialog.getDialogPane().setContent(progress);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        dialog.getDialogPane().setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-radius: 16;" +
                        "-fx-border-color: #E5E7EB;" +
                        "-fx-border-width: 1;" +
                        "-fx-padding: 20;"
        );

        dialog.show();
    }
    @FXML
    private void openNewSearchConfigDialog() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/UrlBuilderDialog.fxml"));

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Neue Jobsuche");
        dialog.getDialogPane().setContent(loader.load());
        UrlSearchDialogController controller = loader.getController();
        dialog.getDialogPane().getButtonTypes().addAll(
                ButtonType.OK,
                ButtonType.CANCEL
        );
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String generatedUrl = controller.buildUrl();

            System.out.println(generatedUrl);
        }
    }
    private void openEditSearchConfigDialog(String url,String portal, String keyword, String postalCode, String radius, boolean isCustom, Long id) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/UrlBuilderDialog.fxml"));

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Suche bearbeiten");
        dialog.getDialogPane().setContent(loader.load());
        UrlSearchDialogController controller = loader.getController();
        controller.editSearch(portal,keyword, postalCode, radius, isCustom);
        dialog.getDialogPane().getButtonTypes().addAll(
                ButtonType.OK,
                ButtonType.CANCEL
        );

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            controller.updateSearch(id, this::loadSearches);
        }
    }


    @FXML
    private void loadJobs() {
        createLoadingDialogJobs();
        taskLoadJob();
    }

    @FXML
    private void loadSearches(){
        createLoadingDialogSearch();
        taskLoadSearch();
    }

    private void taskLoadJob() {
        Task<List<JobEntity>> task = new Task<>() {
            @Override
            protected List<JobEntity> call() {
                JobRepository db = JobRepository.getInstance();
                return db.getAllJobs();
            }
        };

        task.setOnSucceeded(_ -> {
            List<JobEntity> jobs = task.getValue();
            jobContainer.getChildren().clear();


            if(!jobs.isEmpty()) {
                jobs.stream().map(job-> createJobCard(job.getJobTitle(), job.getCompany().getCompanyName(),job.getCompany().getUrl(),job.getApplied()))
                        .forEach(card -> jobContainer.getChildren().add(card));
                System.out.println("Beendet");
                dialog.close();
                alertInformation.showAlert("Erfolgreich", "Jobs gefunden !");
            } else {
                System.out.println("Beendet");
                dialog.close();
                alertInformation.showAlert("Fehler", "Keine Jobs gefunden! Bitte lege eine Suche an");
            }
        });

        task.setOnFailed(_ -> {
            System.out.println("Abgebrochen");
            task.getException().printStackTrace();
            dialog.close();
            alertInformation.showAlert("Fehler", "Keine Jobs gefunden. Bitte legen sie zuerst eine Suche an");
        });

        new Thread(task).start();
    }

    private void taskLoadSearch() {
        Task<List<SearchUrlEntity>> task = new Task<>() {
            @Override
            protected List<SearchUrlEntity> call() {
                SearchUrlRepository db = SearchUrlRepository.getInstance();
                return db.getAllSearchUrls();
            }
        };

        task.setOnSucceeded(_ -> {
            List<SearchUrlEntity> searches = task.getValue();
            searchCardContainer.getChildren().clear();
            if(!searches.isEmpty()) {
                searches.stream().map(search-> createSearchCard(search.getPortal(), search.getKeyword(),search.getUrl(),search.getPostal_code(),search.getRadius(),search.getIsCustom(),search.getId()))
                        .forEach(card -> searchCardContainer.getChildren().add(card));
                System.out.println("Beendet");
                dialog.close();
                alertInformation.showAlert("Erfolgreich", "Suchaufträge gefunden !");
            } else {
                System.out.println("Beendet");
                dialog.close();
                alertInformation.showAlert("Fehler", "Keine Suchaufträge gefunden! Bitte lege eine Suche an");
            }
        });

        task.setOnFailed(_ -> {
            System.out.println("Abgebrochen");
            task.getException().printStackTrace();
            dialog.close();
            alertInformation.showAlert("Fehler", "Keine Jobs gefunden. Bitte legen sie zuerst eine Suche an");
        });

        new Thread(task).start();
    }

    @FXML
        private void crawlJobs(){
            createLoadingDialogUrl();

            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    SearchUrlRepository db = SearchUrlRepository.getInstance();
                    List<SearchUrlEntity> searchUrls = db.getAllSearchUrls();

                    if(!searchUrls.isEmpty()) {
                        for(SearchUrlEntity url :  searchUrls) {
                            CrawlerInterface crawler = CrawlerFactory.createCrawler(url.getUrl());
                            if(crawler instanceof FinanzInformatik) {
                                crawler.crawlJobsiteOneParameter(url.getKeyword());
                            }
                            crawler.crawlJobsiteOneParameter(url.getUrl());
                        }


                    } else {
                          throw new IllegalStateException("No search URLs configured");
                    }


                    return null;
                }
            };

            task.setOnSucceeded(_ -> {
                System.out.println("Beendet");
                dialog.close();
                alertInformation.showAlert("Erfolgreich", "Die Suche war erfolgreich !");
            });

            task.setOnFailed(_ -> {
                System.out.println("Abgebrochen");
                dialog.close();
                task.getException().printStackTrace();
                alertInformation.showAlert("Fehler", task.getException().getMessage());
            });

            new Thread(task).start();

        }
    @FXML
    private void test() {
        /*try {
            String url = "https://www.f-i.de/stellen-finden?FieldOfActivity[]=softwareentwicklung";
            Document doc = Jsoup.connect(url).get();
            Elements items = doc.select("div.list-row div.list-item");

            for (Element item : items) {
                if(item.text().toLowerCase().contains("java")) {
                    System.out.println(item.text());
                }

                if(item.text().toLowerCase().contains("Fullstack")) {
                    System.out.println(item.text());
                }

            }

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage() );
            throw new RuntimeException();
        }*/

    }
}

