package org.ProjectX.factories.Crawler;
import org.ProjectX.config.Constants;
import org.ProjectX.entity.CompanyEntity;
import org.ProjectX.entity.JobEntity;
import org.ProjectX.entity.SearchUrlEntity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FinanzInformatik implements CrawlerInterface{
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

}
