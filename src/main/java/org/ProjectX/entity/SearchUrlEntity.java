package org.ProjectX.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class SearchUrlEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, length = 2000)
    private String url ;

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

    public void setUrl(String url) {
        this.url = url;
    }
    public String getUrl() {
        return url;
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
