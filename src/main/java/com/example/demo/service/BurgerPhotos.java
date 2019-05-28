package com.example.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Service
public class BurgerPhotos {
    @Autowired
    RestTemplate restTemplate;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public HashMap<String, String> findPlaces(String clientId, String clientSecret) throws IOException {
        String jsonString = restTemplate.getForObject(
                "https://api.foursquare.com/v2/venues/explore?client_id="+clientId+"&client_secret="+clientSecret+"&v=20180323&ll=58.378025, 26.728493&query=burger",
                String.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = mapper.readTree(jsonString);
        JsonNode groups = actualObj.get("response").get("groups");
        HashMap<String, String> venueIds = new HashMap<String, String>();
        if (groups.isArray()) {
            for (JsonNode group : groups) {
                JsonNode items = group.get("items");
                if (groups.isArray()) {
                    for (JsonNode item : items) {
                        JsonNode venueIdJson = item.get("venue").get("id");
                        JsonNode venueNameJson = item.get("venue").get("name");
                        venueIds.put(venueNameJson.textValue().toLowerCase()
                                        .replace(" ", "-")
                                        .replaceAll("[èéêë]", "e")
                                        .replaceAll("[ûù]", "u")
                                        .replaceAll("[ïî]", "i")
                                        .replaceAll("[àâ]", "a")
                                        .replaceAll("[Ôô]", "o")
                                , venueIdJson.textValue());
                    }
                }
            }
        }
        return venueIds;
    }

    public HashMap<String, String> getPhotos(HashMap<String, String> places) {
        HashMap<String, String> imageLinks = new HashMap<String, String>();
        for (Map.Entry<String, String> place : places.entrySet()) {
            String key = place.getKey();
            String value = place.getValue();
            HashMap<String, String> photosOfVenue = new HashMap<String, String>();

            try {
                URL url = new URL("https://foursquare.com/v/" + key + "/" + value + "/photos");
                Document document = Jsoup.parse(url, 3000);
                Elements images = document.select("img");
                for (Element image : images) {
                    String link = image.absUrl("src");
                    photosOfVenue.put(link, key);
                }
                String latestPhoto = findBurgersAmongPhotos(photosOfVenue);
                imageLinks.put(latestPhoto, key);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return imageLinks;
    }

    public String findBurgersAmongPhotos(HashMap<String, String> photosOfVenue) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        final String baseUrl = "https://pplkdijj76.execute-api.eu-west-1.amazonaws.com/prod/recognize";
        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray imgL = new JSONArray(photosOfVenue.keySet());
            jsonObject.put("urls", imgL);
            HttpEntity<String> entity = new HttpEntity<>(jsonObject.toString(), headers);
            String jsonString = restTemplate.postForObject(baseUrl, entity, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode actualObj = mapper.readTree(jsonString);
            JsonNode urlWithBurger = actualObj.get("urlWithBurger");
            return urlWithBurger.textValue();
        } catch (org.json.JSONException | IOException | HttpClientErrorException exception) {
            exception.printStackTrace();
        }
        return null;
    }
}


