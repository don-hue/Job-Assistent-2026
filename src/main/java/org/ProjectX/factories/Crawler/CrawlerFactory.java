package org.ProjectX.factories.Crawler;

import org.ProjectX.entity.SearchUrlEntity;

import java.util.concurrent.BlockingQueue;

public class CrawlerFactory {
    public static CrawlerInterface createCrawler(String url, SearchUrlEntity search) {
        if(url.toLowerCase().contains("stepstone")){
            System.out.println("XXX Stepstone");
            return new Stepsstone.Builder()
                    .url(url)
                    .search(search)
                    .build();
        }
        if(url.toLowerCase().contains("commerzbank")){
            System.out.println("XXX Commerzbank");
            return new Commerzbank.Builder()
                    .search(search)
                    .url(url)
                    .build();
        }
        if(url.toLowerCase().contains("f-i.de")){
            System.out.println("XXX FI");
            return new FinanzInformatik.Builder()
                    .search(search)
                    .keyword(search.getKeyword())
                    .build();
        }
        throw new IllegalArgumentException(url);
    };
}
