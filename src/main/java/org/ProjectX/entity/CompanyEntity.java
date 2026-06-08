package org.ProjectX.entity;

import jakarta.persistence.*;

import java.net.URL;
import java.util.List;

@Entity
public class CompanyEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name="name")
    String companyName;

    private URL url;
    private URL api;

    private boolean showCompany;

    @OneToMany(mappedBy = "company")
    private List<JobEntity> jobs;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public URL getApi() {
        return api;
    }

    public void setApi(URL api) {
        this.api = api;
    }

    public List<JobEntity> getJobs() {
        return jobs;
    }

    public void setJobs(List<JobEntity> jobs) {
        this.jobs = jobs;
    }

    public void setShowCompany(boolean showCompany) { this.showCompany = showCompany;}

    public boolean getShowCompany() {return showCompany;}


    public Long getId() {
        return id;
    }
}
