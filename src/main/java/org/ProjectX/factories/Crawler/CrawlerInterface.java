package org.ProjectX.factories.Crawler;

import org.ProjectX.entity.JobEntity;
import org.ProjectX.entity.SearchUrlEntity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public interface CrawlerInterface {
    default List<JobEntity> crawl(){
        List<JobEntity> jobs = new ArrayList<>();
        System.out.println("Default crawl needs to be overwritten");
        return jobs;
    }
}
