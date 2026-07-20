package org.ProjectX.factories.Crawler;


import org.ProjectX.database.JobRepository;
import org.ProjectX.dto.JobDto;
import org.ProjectX.entity.SearchUrlEntity;
import org.ProjectX.service.CrawlerService;

import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Stepsstone implements CrawlerInterface{
    CrawlerService util = CrawlerService.getInstance();
    JobRepository jobDb = JobRepository.getInstance();

    //crawls Stepstonepage for Jobs
    /*public void crawlJobsiteOneParameter(String url){
        int pages = util.howManyPages(url);
        for (int i = 1; i < pages + 1 ; i++) {
            try {
                String pagedURL = url + "&page=" + i;
                Document doc = Jsoup.connect(pagedURL).get();
                Elements links = doc.select("article");

                for (Element article : links) {

                    Element jobDiv = article.selectFirst("h2");
                    Element companyDiv = article.selectFirst("span");

                    if (jobDiv != null && companyDiv!=null) {
                        URL companyUrl = getJobsiteWithUrl(companyDiv.text());
                        JobDto dto = new JobDto(jobDiv.text(), companyDiv.text(), companyUrl);
                        jobDb.saveJob(dto);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage() );
                throw new RuntimeException();
            }
        }

    }*/

    public void crawlJobsiteTwoParameter(String url, SearchUrlEntity search){
        int pages = util.howManyPages(url);
        for (int i = 1; i < pages + 1 ; i++) {
            try {
                String pagedURL = url + "&page=" + i;
                Document doc = Jsoup.connect(pagedURL).get();
                Elements links = doc.select("article");

                for (Element article : links) {

                    Element jobDiv = article.selectFirst("h2");
                    Element companyDiv = article.selectFirst("span");

                    if (jobDiv != null && companyDiv!=null) {
                        URL companyUrl = getJobsiteWithUrl(companyDiv.text());
                        JobDto dto = new JobDto(jobDiv.text(), companyDiv.text(), companyUrl,search);
                        jobDb.saveJob(dto);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage() );
                throw new RuntimeException();
            }
        }

    }
    //crawls Jobpage of a company
    public URL getJobsiteWithUrl(String companyName) {
        try( WebClient webClient = new WebClient()) {
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setCssEnabled(false);
            webClient.addRequestHeader(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/137.0.0.0 Safari/537.36"
            );
            HtmlPage page = webClient.getPage("https://duckduckgo.com/html/?q=" + URLEncoder.encode(companyName, StandardCharsets.UTF_8) + "+Stellenangebote");
            String html = page.asXml();

            Document doc = Jsoup.parse(html);
            Element firstResult = doc.selectFirst("a.result__a");

            if(firstResult != null) {
                return util.convertToURL(firstResult.attr("href"));
            }
            if (doc.text().contains("Unfortunately, bots use DuckDuckGo too")) {
                throw new RuntimeException("DuckDuckGo blocked the request");
            }
            throw new IllegalArgumentException("Error in getJobSite");
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
