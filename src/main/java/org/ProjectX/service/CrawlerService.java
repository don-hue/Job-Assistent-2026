package org.ProjectX.service;
import org.ProjectX.dto.JobDto;
import org.ProjectX.entity.CompanyEntity;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import javax.swing.*;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CrawlerService {
    private static final CrawlerService INSTANCE = new CrawlerService();

    private CrawlerService() {
    }

    ;

    public static CrawlerService getInstance() {
        return INSTANCE;
    }

    public URL convertToURL(String href) {
        String query = href.substring(href.indexOf('?') + 1);
        Map<String, String> params =
                Arrays.stream(query.split("&"))
                        .map(p -> p.split("=", 2))
                        .collect(Collectors.toMap(
                                a -> a[0],
                                a -> a.length > 1 ? a[1] : ""
                        ));

        String realUrl = URLDecoder.decode(
                params.get("uddg"),
                StandardCharsets.UTF_8
        );

        try {
            return URI.create(realUrl).toURL();

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public int howManyPages(String url) {
        int page = 1;
        Boolean goOn = true;

        while (goOn) {
            try {
                String pagedURL = url + "&page=" + page;
                Document doc = Jsoup.connect(pagedURL)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                        .header("Accept", "text/html,application/xhtml+xml")
                        .header("Accept-Language", "de-DE,de;q=0.9")
                        .get();
                page++;


            } catch (IOException e) {
                System.out.println("in StupidCrawler" + e.getMessage());
                page--;
                goOn = false;
            } catch (RuntimeException e) {
                System.out.println("in StupidCrawler" + e.getMessage());
                page--;
                goOn = false;
            }
        }
        return page;
    }

    public List<String> stepstoneSearchToUrls(String stepstoneUrl) {
        int pages = howManyPages(stepstoneUrl);
        List<String> stepstoneUrls = new ArrayList<>();
        try {
            for (int i = 1; i < pages + 1; i++) {
                String pagedURL = stepstoneUrl + "&page=" + i;
                stepstoneUrls.add(pagedURL);
            }
        } catch (RuntimeException e) {
            System.out.println("Error in function" + e.getMessage());
            throw new RuntimeException("Error" + e);
        }

        return stepstoneUrls;
    }

    public URL getJobsiteWithUrl(String companyName) {
        if(companyName == null || companyName.isBlank()) {
            return  null;
        }
        try {
            String query = URLEncoder.encode(companyName + " Stellenangebote", StandardCharsets.UTF_8);
            String searchUrl = "https://duckduckgo.com/html/?q=" + query;


            Document doc = Jsoup.connect(searchUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .header("Accept", "text/html,application/xhtml+xml")
                    .header("Accept-Language", "de-DE,de;q=0.9,en;q=0.8")
                    .header("Referer", "https://duckduckgo.com/")
                    .get();

            if (doc.text().contains("Unfortunately, bots use DuckDuckGo too")) {
                throw new IllegalStateException("DuckDuckGo blocked request for: " + companyName);
            }

            Element firstResult = doc.selectFirst("a.result__a");

            if(firstResult != null) {
                URL url = convertToURL(firstResult.attr("href"));
                return url;
            }
            return null;

        } catch (IOException e) {
            System.out.println("XXX");
            throw new RuntimeException("Search failed for" + companyName,e);
        }
    }
}
