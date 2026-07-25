package org.ProjectX.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class SearchUrlEntity {
    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "search")
    private List<JobEntity> jobs;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "search_urls",
            joinColumns = @JoinColumn(name = "search_id")
    )
    @Column(name = "url", length = 2000)
    private List<String> urls = new ArrayList<>();

    @Column
    private String keyword;

    @Column
    private String portal;
    @Column
    private String postal_code;
    @Column
    private String radius;
    @Column
    private Boolean isCustom;

    public void setUrl(List<String> urls) {
        this.urls = urls;
    }
    public List<String> getUrl() {
        return urls;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    public String getKeyword() {
        return keyword;
    }

    public void setPortal( String portal) {
        this.portal = portal;
    }
    public String getPortal(){return portal;}

    public void setPostal_code(String postalCode) {
        this.postal_code = postalCode;
    }
    public String getPostal_code(){return postal_code;}

    public void setRadius(String radius) {
        this.radius = radius;
    }
    public String getRadius() {return radius;}
    public void setIsCustom(boolean isCustom) {
        this.isCustom = isCustom;
    }
    public boolean getIsCustom(){
        return isCustom;
    }

    public Long getId(){return id;}
}
