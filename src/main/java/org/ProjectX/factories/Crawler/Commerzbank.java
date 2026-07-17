package org.ProjectX.factories.Crawler;

import org.ProjectX.database.JobRepository;
import org.ProjectX.dto.JobDto;
import org.ProjectX.service.CrawlerService;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Commerzbank implements CrawlerInterface{
    CrawlerService util = CrawlerService.getInstance();
    JobRepository jobDb = JobRepository.getInstance();

    //crawl Commerzbank API for Jobs
    public void crawlJobsiteOneParameter(String url){
        try (WebClient webClient = new WebClient()) {
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setCssEnabled(false); // faster
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            final String jobsJson = webClient
                    .getPage(url)
                    .getWebResponse().getContentAsString();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jobsJson);
            JsonNode jobs = root.path("SearchResult")
                    .path("SearchResultItems");

            URL jobpage = getJobsiteWithUrl("Commerzbank");
            for (JsonNode job : jobs) {
                String title = job.get("MatchedObjectDescriptor")
                        .get("PositionTitle")
                        .asText();

                JsonNode location = job.path("MatchedObjectDescriptor")
                        .path("PositionLocation");

                String cityName = location.get(0)
                        .path("CityName")
                        .asText();
                JobDto dto = new JobDto(title, "Commerzbank - " + cityName, jobpage);
                jobDb.saveJob(dto);
            }
        } catch (IOException e) {
            System.out.println("Error accessing: ");
        }
    }

    //get Commerzbank Homepage
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
