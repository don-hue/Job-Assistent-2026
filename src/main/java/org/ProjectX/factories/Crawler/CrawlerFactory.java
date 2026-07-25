package org.ProjectX.factories.Crawler;

import org.ProjectX.entity.SearchUrlEntity;

public class CrawlerFactory {
    public static CrawlerInterface createCrawler(String url, SearchUrlEntity search) {
        if(url.toLowerCase().contains("stepstone")){
            return new Stepsstone.Builder()
                    .url(url)
                    .search(search)
                    .build();
        }
        if(url.toLowerCase().contains("commerzbank")){
            return new Commerzbank.Builder()
                    .search(search)
                    .url(url)
                    .build();
        }
        if(url.toLowerCase().contains("f-i.de")){
            return new FinanzInformatik.Builder()
                    .search(search)
                    .keyword(search.getKeyword())
                    .build();
        }
        throw new IllegalArgumentException(url);
    }
}
