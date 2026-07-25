package org.ProjectX.factories.Crawler;


import org.ProjectX.entity.CompanyEntity;
import org.ProjectX.entity.JobEntity;
import org.ProjectX.entity.SearchUrlEntity;
import org.ProjectX.service.CrawlerService;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Stepsstone implements CrawlerInterface{
    CrawlerService util = CrawlerService.getInstance();
    private final String url;
    private final SearchUrlEntity search;

    public Stepsstone(Builder builder) {
        this.url = builder.url;
        this.search = builder.search;
    }


    public static class Builder{
        private String url;
        private SearchUrlEntity search;

        public  Builder url(String url){
            this.url = url;
            return this;
        }

        public Builder search(SearchUrlEntity search) {
            this.search = search;
            return this;
        }
        public Stepsstone build(){
            return new Stepsstone(this);
        }
    }

    //crawls Stepstonepage for Jobs
/*    public void crawlJobsiteTwoParameter(String url, SearchUrlEntity search){
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

    }*/

    public List<JobEntity> crawl(){
        List<JobEntity> jobs = new ArrayList<>();
            try {
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.0")
                        .timeout(5000)
                        .get();
                Elements articles = doc.select("article");

                for (Element article : articles) {
                    Element jobDiv = article.selectFirst("h2");
                    Element companyDiv = article.selectFirst("span");

                    if(jobDiv == null || companyDiv == null){
                        continue;
                    }

                    String jobTitle = jobDiv.text().trim();
                    String companyName = companyDiv.text().trim();

                    if(jobTitle.isEmpty() || companyName.isEmpty()) {
                        continue;
                    }

                    CompanyEntity company = new CompanyEntity();
                    company.setCompanyName(companyName);
                    company.setShowCompany(true);

                    JobEntity job = new JobEntity();
                    job.setJobTitle(jobTitle);
                    job.setCompany(company);
                    job.setSearch(search);
                    job.setApplied(false);
                    jobs.add(job);

                }
            } catch (IOException e) {
                throw new RuntimeException("Crawl failed" + url, e);
            }
            return jobs;
    }
    //crawls Jobpage of a company
    public URL getJobsiteWithUrl(String companyName) {
        /*try( WebClient webClient = new WebClient()) {
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
        }*/
        if(companyName == null || companyName.isBlank()) {
            return  null;
        }
        try {
            String query = URLEncoder.encode(companyName + " Stellenangebote", StandardCharsets.UTF_8);
            String searchUrl = "https://lite.duckduckgo.com/lite/?q=" + query;
            Thread.sleep(2000 + (int)(Math.random() * 3000));

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
