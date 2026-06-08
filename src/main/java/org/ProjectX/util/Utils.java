package org.ProjectX.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class Utils {
    private static final  Utils INSTANCE = new Utils();
    private Utils() {};

    public static Utils getInstance() {
        return INSTANCE;
    }

    public boolean isValidURL(String urlString) {
        try {
            URL url = new URL(urlString);
            url.toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
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

        String realUrl= URLDecoder.decode(
                params.get("uddg"),
                StandardCharsets.UTF_8
        );

        try {
            return URI.create(realUrl).toURL();

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
    public int howManyPages (String url) {
        int page = 1;
        Boolean goOn = true;

        while(goOn) {
            try {
                String pagedURL = url + "&page=" + page;
                Document doc = Jsoup.connect(pagedURL).get();
                page++;

            } catch (IOException e) {
                System.out.println("in StupidCrawler" + e.getMessage() );
                page--;
                goOn = false;
            }
        }
        return page;
    }
}
