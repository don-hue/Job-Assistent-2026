package org.ProjectX.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.ProjectX.database.CompanyRepository;
import org.ProjectX.entity.CompanyEntity;
import org.ProjectX.service.CrawlerService;
import org.ProjectX.util.Utils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class WelcomeScreenController {

    @FXML
    private TextField appTitle;

    @FXML
    public void initialize() {
        appTitle.setFocusTraversable(false);
    }

    @FXML
    private void startProgram(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/MainScreen.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
        /*try {
            CompanyRepository db = CompanyRepository.getInstance();
            CrawlerService crawlerService = CrawlerService.getInstance();
            List<CompanyEntity> companies = db.getAllCompanies();


                System.out.println(companies.getFirst().getCompanyName());
                URL companyURL= crawlerService.getJobsiteWithUrl("andrena objects ag");
                System.out.println(companyURL);


        } catch (RuntimeException e) {
            throw new RuntimeException("CrawlCompnayUrlTask failed" + e);
        }*/
    }

    public URL getJobsiteWithUrl(String companyName) {
        CrawlerService util = CrawlerService.getInstance();
        if(companyName == null || companyName.isBlank()) {
            return  null;
        }
        try {
            String query = URLEncoder.encode(companyName + " Stellenangebote", StandardCharsets.UTF_8);
            String searchUrl = "https://duckduckgo.com/html/?q=" + query;

            Document doc = Jsoup.connect(searchUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .header("Accept", "text/html,application/xhtml+xml")
                    .header("Accept-Language", "de-DE,de;q=0.9,en;q=0.8")
                    .header("Referer", "https://duckduckgo.com/")
                    .get();

            if (doc.text().contains("Unfortunately, bots use DuckDuckGo too")) {
                throw new IllegalStateException("DuckDuckGo blocked request for: " + companyName);
            }

            Element firstResult = doc.selectFirst("a.result__a");

            if(firstResult != null) {
                return util.convertToURL(firstResult.attr("href"));
            }

            return null;

        } catch (IOException e) {
            throw new RuntimeException("Search failed for" + companyName,e);
        }
    }
}
