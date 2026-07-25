package org.ProjectX.factories.Crawler;

import org.ProjectX.database.JobRepository;
import org.ProjectX.dto.JobDto;
import org.ProjectX.entity.CompanyEntity;
import org.ProjectX.entity.JobEntity;
import org.ProjectX.entity.SearchUrlEntity;
import org.ProjectX.service.CrawlerService;
import org.htmlunit.WebClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Commerzbank implements CrawlerInterface{
    CrawlerService util = CrawlerService.getInstance();
    JobRepository jobDb = JobRepository.getInstance();
    private final String url;
    private final SearchUrlEntity search;
    //crawl Commerzbank API for Jobs
    public Commerzbank(Builder builder){
        this.url = builder.url;
        this.search = builder.search;
    }

    public static class Builder {
        private String url;
        private SearchUrlEntity search;


        public Builder url(String url){
            this.url = url;
            return this;
        }

        public Builder search(SearchUrlEntity search) {
            this.search =search;
            return  this;
        }

        public Commerzbank build() {
            return new Commerzbank(this);
        }
    }

    public List<JobEntity> crawl(){
        List<JobEntity> jobEntityList = new ArrayList<>();
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

            for (JsonNode job : jobs) {
                String title = job.get("MatchedObjectDescriptor")
                        .get("PositionTitle")
                        .asText();

                JsonNode location = job.path("MatchedObjectDescriptor")
                        .path("PositionLocation");

                String cityName = location.get(0)
                        .path("CityName")
                        .asText();

                CompanyEntity company = new CompanyEntity();
                company.setCompanyName("Commerzbank - " + cityName);
                company.setShowCompany(true);


                JobEntity jobEntity = new JobEntity();
                jobEntity.setJobTitle(title);
                jobEntity.setCompany(company);
                jobEntity.setSearch(search);
                jobEntity.setApplied(false);
                jobEntityList.add(jobEntity);

            }
        } catch (IOException e) {
            System.out.println("Error accessing: ");
        }
        return jobEntityList;
    }

    public void crawlJobsiteTwoParameter(String url, SearchUrlEntity search){
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
                JobDto dto = new JobDto(title, "Commerzbank - " + cityName, jobpage,search);
                jobDb.saveJob(dto);
            }
        } catch (IOException e) {
            System.out.println("Error accessing: ");
        }
    }

    //get Commerzbank Homepage
    public URL getJobsiteWithUrl(String companyName) {
       /* try( WebClient webClient = new WebClient()) {
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
        }*/

        if(companyName == null || companyName.isBlank()) {
            return  null;
        }
        try {
            String query = URLEncoder.encode(companyName + " Stellenangebote", StandardCharsets.UTF_8);
            String searchUrl = "https://duckduckgo.com/html/?q=" + query;
            Thread.sleep(500 + (int)(Math.random() * 3000));

            Document doc = Jsoup.connect(searchUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .header("Accept", "text/html,application/xhtml+xml")
                    .header("Accept-Language", "de-DE,de;q=0.9,en;q=0.8")
                    .header("Referer", "https://duckduckgo.com/")
                    .timeout(15000)
                    .get();

            if (doc.text().contains("Unfortunately, bots use DuckDuckGo too")) {
                throw new IllegalStateException("DuckDuckGo blocked request for: " + companyName);
            }

            Element firstResult = doc.selectFirst("a.result__a");

            if(firstResult != null) {
                return util.convertToURL(firstResult.attr("href"));
            }

            return null;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } catch (IOException e) {
            throw new RuntimeException("Search failed for" + companyName,e);
        }
    }


}
