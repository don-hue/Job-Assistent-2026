package org.ProjectX;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

   public static void main(String[] args) {
//        int maxPages = 10; // limit to avoid crawling too much
//        String startUrl = "https://www.stepstone.de/jobs/software-entwickler-in/in-altena?radius=5&searchOrigin=Resultlist_top-search&whereType=autosuggest&q=Software-Entwickler%2Fin"; // seed URL
//
//        StupidCrawler crawler = new StupidCrawler(maxPages);
//        CompanyCrawler companyCrawler = new CompanyCrawler();
//        //crawler.crawl(startUrl);
//
//        //companyCrawler.homePage();
//        //test.show();


        launch(args);
//
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/WelcomeScreen.fxml"));
        Scene welcomeScene = new Scene(root);
        stage.setScene(welcomeScene);
        stage.show();
    }


}