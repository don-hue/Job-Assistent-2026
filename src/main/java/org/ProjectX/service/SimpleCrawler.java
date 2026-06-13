package org.ProjectX.service;

import org.ProjectX.Database.CompanyRepository;
import org.ProjectX.Database.JobRepository;

import org.ProjectX.dto.JobDto;
import org.ProjectX.entity.CompanyEntity;
import org.ProjectX.util.Utils;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

import java.util.List;


public class SimpleCrawler {
    CompanyRepository companyDb = CompanyRepository.getInstance();
    JobRepository jobDb = JobRepository.getInstance();
    Utils util = Utils.getInstance();

    public void crawl(String url) {
        if(url.toLowerCase().contains("stepstone")) {
            crawlStepstone(url);
        }
    }

    private void crawlStepstone(String url) {
        int pages = util.howManyPages(url);
        for (int i = 1; i < pages + 1 ; i++) {
            try {
                String pagedURL = url + "&page=" + i;
                Document doc = Jsoup.connect(pagedURL).get();
                Elements links = doc.select("article");

                for (Element article : links) {

                    Element jobDiv = article.selectFirst("h2");
                    Element companyDiv = article.selectFirst("span");

                    if (jobDiv != null && companyDiv!=null) {
                        JobDto dto = new JobDto(jobDiv.text(), companyDiv.text());
                        jobDb.saveJobs(dto);
                    }
                }

                findCompanyURL();
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage() );
                throw new RuntimeException();
            }
        }
    }

    public void findCompanyURL() {
        List<CompanyEntity> companies = companyDb.getCompaniesWithoutUrl();
        companies.forEach(company -> {
            crawlCompanyURL(company.getCompanyName(), company.getId());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    private void crawlCompanyURL(String companyName, Long id) {
        WebClient webClient = new WebClient();
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setCssEnabled(false);

        webClient.addRequestHeader(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/137.0.0.0 Safari/537.36"
        );

        try {
            HtmlPage page = webClient.getPage("https://duckduckgo.com/html/?q=" + URLEncoder.encode(companyName, StandardCharsets.UTF_8) + "+Stellenangebote");
            String html = page.asXml();

            Document doc = Jsoup.parse(html);
            Element firstResult = doc.selectFirst("a.result__a");

            if (firstResult != null) {
                URL url = util.convertToURL(firstResult.attr("href"));
                companyDb.updateCompanyURL(url, id);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

