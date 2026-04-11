package org.ProjectX.dto;

/*public class CrawlerDto {
    String job;
    String company;

    public CrawlerDto(String job, String company) {
        this.job = job;
        this.company = company;
    }

    public String getJob() {
        return job;
    }

    public String getCompany() {
        return company;
    }

    @Override
    public String toString() {
        return "CrawlerDto{" +
                "job='" + job + '\'' +
                ", company='" + company + '\'' +
                '}';
    }
}*/
public record CrawlerDto(String job, String company) {}
