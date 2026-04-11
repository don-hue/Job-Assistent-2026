package org.ProjectX.service;

import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlPage;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

public class CompanyCrawler {
    String url = "https://api-jobs.commerzbank.com/search/?data={\"LanguageCode\":\"DE\",\"SearchParameters\":{\"FirstItem\":1,\"CountItem\":10000,\"Sort\":[{\"Criterion\":\"PublicationStartDate\",\"Direction\":\"DESC\"}],\"MatchedObjectDescriptor\":[\"ID\",\"PositionTitle\",\"PositionURI\",\"PositionShortURI\",\"PositionLocation.CountryName\",\"PositionLocation.CityName\",\"PositionLocation.Longitude\",\"PositionLocation.Latitude\",\"PositionLocation.PostalCode\",\"PositionLocation.StreetName\",\"PositionLocation.BuildingNumber\",\"PositionLocation.Distance\",\"JobCategory.Name\",\"PublicationStartDate\",\"ParentOrganizationName\",\"OrganizationShortName\",\"CareerLevel.Name\",\"JobSector.Name\",\"PositionIndustry.Name\",\"PublicationCode\"]},\"SearchCriteria\":[{\"CriterionName\":\"JobCategory.Code\",\"CriterionValue\":[\"10\"]},{\"CriterionName\":\"PositionLocation.City\",\"CriterionValue\":[\"107\",\"201\"]},{\"CriterionName\":\"PublicationChannel.Code\",\"CriterionValue\":[\"12\"]},{\"CriterionName\":\"PositionLocation.Distance\",\"CriterionValue\":[\"25\"]}]}";
    public void homePage() {
        try (WebClient webClient = new WebClient()) {
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setCssEnabled(false); // faster
            webClient.getOptions().setThrowExceptionOnScriptError(false);

            final String jobs = webClient
                    .getPage(url)
                    .getWebResponse().getContentAsString();

            fromJsonToData(jobs);
            //System.out.println(jobs);
        }
        catch (IOException e) {
            System.out.println("Error accessing: ");
        }
    }

    private void fromJsonToData (String jobsJson) {
        ObjectMapper mapper = new ObjectMapper();

        JsonNode root = mapper.readTree(jobsJson);
        //JsonNode jobs = root.get("data");
        JsonNode jobs = root.get("SearchResult").get("SearchResultItems");

        for (JsonNode job : jobs) {
            String title = job.get("MatchedObjectDescriptor")
                    .get("PositionTitle")
                    .asText();

            System.out.println("XXX " + title);
            System.out.println("-------------");
        }
        //System.out.println("XXX" + jobs);

    }
}
