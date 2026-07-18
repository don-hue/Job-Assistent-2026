package org.ProjectX.entity;

import jakarta.persistence.*;

@Entity
public class JobEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name="job_title")
    private String jobTitle ;
    @Column()
    private boolean applied ;

    @ManyToOne
    @JoinColumn(name="company_id")
    private CompanyEntity company;

    @ManyToOne
    @JoinColumn(name="search_id")
    private SearchUrlEntity search;

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

    public void setApplied(boolean applied){
        this.applied = applied;
    }

    public boolean getApplied(){
        return this.applied;
    }

    public SearchUrlEntity getSearch() {
        return search;
    }
}
