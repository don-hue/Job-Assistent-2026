package org.ProjectX.factories.Crawler;
import org.ProjectX.database.JobRepository;
import org.ProjectX.config.Constants;
import org.ProjectX.dto.JobDto;
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

public class FinanzInformatik implements CrawlerInterface{
    JobRepository db = JobRepository.getInstance();
    CrawlerService util = CrawlerService.getInstance();
    private final String keyword;
    private final SearchUrlEntity search;


    private FinanzInformatik(Builder builder) {
        this.keyword = builder.keyword;
        this.search = builder.search;
    }

    public static class Builder {
        private String keyword;
        private SearchUrlEntity search;

        public Builder keyword(String keyword){
            this.keyword =keyword;
            return  this;
        }

        public Builder search(SearchUrlEntity search) {
            this.search = search;
            return this;
        }

        public FinanzInformatik build(){
            return new FinanzInformatik(this);
        }
    }

    @Override
    public List<JobEntity> crawl() {
        List<JobEntity> jobs = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(Constants.FinanzInformatik_Jobpage).get();
            Elements items = doc.select("div.list-row div.list-item");

            for (Element item : items) {
                if(item.text().toLowerCase().contains(keyword.toLowerCase())) {

                    CompanyEntity company = new CompanyEntity();
                    company.setCompanyName(Constants.FI_COMPANY_NAME);
                    company.setShowCompany(true);

                    JobEntity job = new JobEntity();
                    job.setJobTitle(item.text());
                    job.setCompany(company);
                    job.setSearch(search);
                    job.setApplied(false);
                    jobs.add(job);
                }
            }

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage() );
            throw new RuntimeException();
        }
        return jobs;
    }

    public void crawlJobsiteTwoParameter(String keyword, SearchUrlEntity search) {
        try {
            Document doc = Jsoup.connect(Constants.FinanzInformatik_Jobpage).get();
            Elements items = doc.select("div.list-row div.list-item");

            for (Element item : items) {
                if(item.text().toLowerCase().contains(keyword.toLowerCase())) {
                    URL jobUrl = getJobsiteWithUrl(Constants.FI_COMPANY_NAME);
                    JobDto jobDto = new JobDto(item.text(),Constants.FI_COMPANY_NAME, jobUrl,search);
                    db.saveJob(jobDto);
                }
            }

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage() );
            throw new RuntimeException();
        }
    }
    public URL getJobsiteWithUrl(String companyName) {
        if(companyName == null || companyName.isBlank()) {
            return  null;
        }
        try {
            String query = URLEncoder.encode(companyName + " Stellenangebote", StandardCharsets.UTF_8);
            String searchUrl = "https://duckduckgo.com/html/?q=" + query;
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
