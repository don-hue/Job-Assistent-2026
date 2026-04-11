package org.ProjectX.service;

import org.ProjectX.dto.CrawlerDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StupidCrawler {

    private final int maxPages;

    public StupidCrawler(int maxPages) {
        this.maxPages = maxPages;
    }

    public List<CrawlerDto> crawl(String url) {
        List<CrawlerDto> crawledData = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(url).get();

            Elements links = doc.select("article");
            Element generatedClassName = doc.select("article").first();

            for (Element article : links) {

                Element jobDiv = article.selectFirst("h2");
                Element companyDiv = article.selectFirst("span");

                if (jobDiv != null && companyDiv!=null) {
                    crawledData.add(
                            new CrawlerDto(jobDiv.text(), companyDiv.text())
                    );

                    System.out.println("Crawled Data :" + crawledData);

                }
            }

        } catch (IOException e) {
            System.out.println("Error accessing: " + url);
        }
        return crawledData;
    }
}
