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
}
