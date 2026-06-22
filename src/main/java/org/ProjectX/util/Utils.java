package org.ProjectX.util;

import org.ProjectX.config.Constants;
import org.htmlunit.WebClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

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
    public double[] getGeoData(String postalCode) {
        try (WebClient webClient = new WebClient()) {

            webClient.getOptions().setJavaScriptEnabled(false);
            webClient.getOptions().setCssEnabled(false);

            String url =
                    Constants.GEO_API
                            + "?postalcode=" + postalCode
                            + "&country=Germany"
                            + "&format=json";

            final String json = webClient
                    .getPage(url)
                    .getWebResponse().getContentAsString();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            JsonNode first = root.get(0);
            double lat = first.path("lat").asDouble();
            double lon = first.path("lon").asDouble();


            System.out.println("Lat: " + lat);
            System.out.println("Lon: " + lon);
            return new double[]{lat, lon};

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void add(
            ArrayNode criteria,
            ObjectMapper mapper,
            String name,
            String value
    ) {
        ObjectNode obj = mapper.createObjectNode();
        obj.put("CriterionName", name);

        ArrayNode arr = mapper.createArrayNode();
        arr.add(value);

        obj.set("CriterionValue", arr);

        criteria.add(obj);
    }
}
