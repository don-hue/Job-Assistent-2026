package org.ProjectX.factories.Crawler;

public class CrawlerFactory {
    public static CrawlerInterface createCrawler(String url) {
        if(url.toLowerCase().contains("stepstone")){
            return new Stepsstone();
        }
        if(url.toLowerCase().contains("commerzbank")){
            return new Commerzbank();
        }
        throw new IllegalArgumentException(url);
    };
}
