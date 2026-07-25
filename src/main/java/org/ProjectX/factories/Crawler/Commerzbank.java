package org.ProjectX.factories.Crawler;
import org.ProjectX.entity.CompanyEntity;
import org.ProjectX.entity.JobEntity;
import org.ProjectX.entity.SearchUrlEntity;
import org.htmlunit.WebClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Commerzbank implements CrawlerInterface{

    private final String url;
    private final SearchUrlEntity search;
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



}
