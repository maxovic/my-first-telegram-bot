package com.vlife.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class DogApiService {
    private static final String BREED_LIST_URL = "https://dog.ceo/api/breeds/list/all";
    private static final String RANDOM_IMAGE_BY_BREED_URL = "https://dog.ceo/api/breed/%s/images/random";
    private static final String NOT_FOUND_IMAGE_URL = "https://www.salonlfc.com/wp-content/uploads/2018/01/image-not-found-scaled-1150x647.png";
    private static final String RESPONSE_KEY = "message";

    public List<String> getAllBreeds() {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();
        String stringJSONResponse = restTemplate.getForObject(BREED_LIST_URL, String.class);
        try {
            Map<String, Map<String, String[]>> mapJSONResponse = mapper.readValue(stringJSONResponse, HashMap.class);
            return new ArrayList<>(mapJSONResponse.get(RESPONSE_KEY).keySet());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public String getRandomImageURLByBreed(String breed) {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();
        String stringJSONResponse = restTemplate.getForObject(
                String.format(RANDOM_IMAGE_BY_BREED_URL, breed),
                String.class
        );
        try {
            Map<String, String> mapJSONResponse = mapper.readValue(stringJSONResponse, HashMap.class);
            return mapJSONResponse.get(RESPONSE_KEY);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return NOT_FOUND_IMAGE_URL;
    }
}
