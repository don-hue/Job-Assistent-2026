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
    default void crawlJobSite(){
        System.out.println("Default crawlJobSite needs to be overwritten");
    }

    default void crawlJobsiteOneParameter(String url) {
        System.out.println("Default crawlJobsiteOneParameter needs to be overwritten");
    }

    default URL getJobsiteWithUrl(String url) throws MalformedURLException {
        System.out.println("Default crawlJobsiteWithUrl needs to be overwritten");
        return new URL("https://duckduckgo.com");
    }

    default void crawlJobsiteTwoParameter(String string, SearchUrlEntity search) {
        System.out.println("Default crawlJobsiteTwoParameter needs to be overwritten");
    }
}
