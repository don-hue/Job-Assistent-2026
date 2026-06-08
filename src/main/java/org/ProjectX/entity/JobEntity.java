package org.ProjectX.entity;

import jakarta.persistence.*;

@Entity
public class JobEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name="job_title")
    private String jobTitle ;

    @ManyToOne
    @JoinColumn(name="company_id")
    private CompanyEntity company;

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public CompanyEntity getCompany() {
        return company;
    }

    public void setCompany(CompanyEntity company) {
        this.company = company;
    }
}
