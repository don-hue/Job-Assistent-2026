package org.ProjectX.controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
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
import org.ProjectX.Database.CompanyRepository;
import org.ProjectX.Database.JobRepository;
import org.ProjectX.Database.SearchUrlRepository;
import org.ProjectX.entity.JobEntity;
import org.ProjectX.entity.SearchUrlEntity;
import org.ProjectX.service.SimpleCrawler;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

public class MainScreenController {
    @FXML
    private VBox cardContainer;
    @FXML
    private TextFlow textFlow;
    @FXML
    private ScrollPane centerScrollPane;
    Dialog<Void> dialog = new Dialog<>();

    @FXML
    public void initialize() {
        clipScrollPane();
        tutorialText();

    }

    private HBox createCard(String jobTitle, String company, URL url) {
        HBox card = new HBox(15);
        VBox content = createLabelsForCard(jobTitle, company, url);
        HBox checkboxContainer = createCheckboxForCard(company);
        Region spacer = new Region();
        card.setStyle(
                        "-fx-background-color: white;" +
                        "-fx-padding: 15;" +
                        "-fx-background-radius: 10;" +
                        "-fx-effect: drop shadow(gaussian, rgba(0,0,0,0.15), 8,0,0,4);"
        );
        card.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(spacer, Priority.ALWAYS);
        card.getChildren().addAll(content, spacer,checkboxContainer);


        return card;
    }
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
    private HBox createCheckboxForCard(String company) {
        HBox checkboxContainer = new HBox(5);
        CheckBox checkbox = new CheckBox();

        checkboxContainer.setAlignment(Pos.BOTTOM_CENTER);
        HBox.setMargin(checkbox, new Insets(0, 0, 5, 0));
        Label checkboxTitle = new Label("Unternehmen ignorieren");
        checkboxTitle.setStyle("-fx-font-size: 16px;");

        checkbox.setOnAction(_ -> {
            if (checkbox.isSelected()) {
                showAlertWithButton(company);
            }
        });
        checkboxContainer.getChildren().addAll(checkbox, checkboxTitle);

        return checkboxContainer;
    }
    private void clipScrollPane() {
        Rectangle clip = new Rectangle();

        clip.widthProperty().bind(centerScrollPane.widthProperty());
        clip.heightProperty().bind(centerScrollPane.heightProperty());

        clip.setArcWidth(42);
        clip.setArcHeight(42);

        centerScrollPane.setClip(clip);
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

    @FXML
    private void openSearchConfigDialog() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/UrlBuilderDialog.fxml"));

        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.setTitle("Neue Jobsuche");

        dialog.getDialogPane().setContent(loader.load());

        dialog.getDialogPane().getButtonTypes().addAll(
                ButtonType.OK,
                ButtonType.CANCEL
        );

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {

            UrlSearchDialogController controller =
                    loader.getController();

            String generatedUrl = controller.buildUrl();

            System.out.println(generatedUrl);
        }
    }


    @FXML
    private void loadJobs() {
        createLoadingDialogJobs();
        Task<List<JobEntity>> task = new Task<>() {
            @Override
            protected List<JobEntity> call() {
                JobRepository db = JobRepository.getInstance();
                return db.getAllJobs();
            }
        };

        task.setOnSucceeded(_ -> {
            List<JobEntity> jobs = task.getValue();
            cardContainer.getChildren().clear();


            if(!jobs.isEmpty()) {
                jobs.stream().map(job->createCard(job.getJobTitle(), job.getCompany().getCompanyName(),job.getCompany().getUrl()))
                        .forEach(card -> cardContainer.getChildren().add(card));
                System.out.println("Beendet");
                dialog.close();
                showAlert("Erfolreich", "Jobs gefunden !");
            } else {
                System.out.println("Beendet");
                dialog.close();
                showAlert("Fehler", "Keine Jobs gefunden! Bitte lege eine Suche an");
            }
        });

        task.setOnFailed(_ -> {
            System.out.println("Abgebrochen");
            task.getException().printStackTrace();
            dialog.close();
            showAlert("Fehler", "Keine Jobs gefunden. Bitte legen sie zuerst eine Suche an");
        });

        new Thread(task).start();
    }

    @FXML
    private void crawlJobs(){
        createLoadingDialogUrl();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                  SimpleCrawler crawler = new SimpleCrawler();
                  SearchUrlRepository db = SearchUrlRepository.getInstance();
                  List<SearchUrlEntity> searchUrls = db.getAllSearchUrls();

                  if(!searchUrls.isEmpty()) {
                      for(SearchUrlEntity url :  searchUrls) {
                          crawler.crawl(url.getUrl());
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
            showAlert("Erfolreich", "Die Suche war erfolgreich !");
        });

        task.setOnFailed(_ -> {
            System.out.println("Abgebrochen");
            dialog.close();
            task.getException().printStackTrace();
            showAlert("Fehler", "Es gab einen Fehler. Bitte probiere es später nochmal.");
        });

        new Thread(task).start();

    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlertWithButton(String company) {
        CompanyRepository db = CompanyRepository.getInstance();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Bestätigung");
        alert.setHeaderText("Wollen sie das Unternehmen gänzlich aus alles Suchen entfernen ?");
        alert.setContentText("Das Löschen führt zu einem endgültigen Entfernen des Unternehmens aus der Suche. " +
                "Zukünftige Suchen werden diesen Filter nutzen. " +
                "In dieser Version kann der Filter nur durch komplettes Löschen alles Suchen zurückgesetzt werden.");
        alert.showAndWait()
                .filter(response -> response == ButtonType.OK)
                .ifPresent(_ -> {
                    db.updateCompany(company);
                    loadJobs();
                });
    }



}

