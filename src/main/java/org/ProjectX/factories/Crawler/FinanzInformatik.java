package org.ProjectX.factories.Crawler;
import org.ProjectX.database.JobRepository;
import org.ProjectX.config.Constants;
import org.ProjectX.dto.JobDto;
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

public class FinanzInformatik implements CrawlerInterface{
    JobRepository db = JobRepository.getInstance();
    CrawlerService util = CrawlerService.getInstance();
    public void crawlJobsiteOneParameter(String keyword) {
        try {
            Document doc = Jsoup.connect(Constants.FinanzInformatik_Jobpage).get();
            Elements items = doc.select("div.list-row div.list-item");

            for (Element item : items) {
                if(item.text().toLowerCase().contains(keyword.toLowerCase())) {
                    URL jobUrl = getJobsiteWithUrl(Constants.FI_COMPANY_NAME);
                    JobDto jobDto = new JobDto(item.text(),Constants.FI_COMPANY_NAME, jobUrl);
                    db.saveJob(jobDto);
                }
            }

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage() );
            throw new RuntimeException();
        }
    }
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
                throw new RuntimeException("Die Suche hat zu viele Anfragen versendet. Bitte versuche es gleich nochmal");
            }
            throw new IllegalArgumentException("Error in getJobSite:");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
