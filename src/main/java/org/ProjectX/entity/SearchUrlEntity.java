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

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }


}
