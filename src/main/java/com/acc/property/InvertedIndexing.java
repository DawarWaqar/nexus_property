package com.acc.property;

import java.io.*;
import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class InvertedIndexing {

    private Map<String, Set<String>> bathsIndex;
    private Map<String, Set<String>> cityIndex;
    private Map<String, Set<String>> priceIndex;
    private Map<String, Set<String>> propertyTypeIndex;
    private Map<String, Set<String>> locationIndex;
    private Map<String, Set<String>> bedsIndex;

    private Map<String, JsonNode> listingMap;

    public InvertedIndexing() {
        bathsIndex = new HashMap<>();
        cityIndex = new HashMap<>();
        priceIndex = new HashMap<>();
        propertyTypeIndex = new HashMap<>();
        locationIndex = new HashMap<>();
        bedsIndex = new HashMap<>();

        listingMap = new HashMap<>();
    }

    public void buildIndex(String jsonFilePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File(jsonFilePath));

        for (JsonNode listing : root) {
            String id = listing.get("id").asText();
            String baths = listing.get("baths").asText();
            String city = listing.get("city").asText();
            String price = listing.get("price").asText();
            String propertyType = listing.get("propertyType").asText();
            String location = listing.get("location").asText();
            String beds = listing.get("beds").asText();

            // Build indexes for each attribute
            bathsIndex.computeIfAbsent(baths, k -> new HashSet<>()).add(id);
            cityIndex.computeIfAbsent(city, k -> new HashSet<>()).add(id);
            priceIndex.computeIfAbsent(price, k -> new HashSet<>()).add(id);
            propertyTypeIndex.computeIfAbsent(propertyType, k -> new HashSet<>()).add(id);
            locationIndex.computeIfAbsent(location, k -> new HashSet<>()).add(id);
            bedsIndex.computeIfAbsent(beds, k -> new HashSet<>()).add(id);

            // Store the listing JSON node
            listingMap.put(id, listing);
        }
    }

    public Set<String> search(Map<String, Object> filters) {
        Set<String> result = null;
        boolean first = true;

        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            String attribute = entry.getKey();
            Object value = entry.getValue();
            Set<String> ids = null;

            // Retrieve IDs from the appropriate index based on the attribute
            if ("baths".equals(attribute)) {
                ids = bathsIndex.get(value);
            } else if ("city".equals(attribute)) {
                ids = cityIndex.get(value);
            } else if ("price".equals(attribute)) {
                ids = priceIndex.get(value);
            } else if ("propertyType".equals(attribute)) {
                ids = propertyTypeIndex.get(value);
            } else if ("location".equals(attribute)) {
                ids = locationIndex.get(value);
            } else if ("beds".equals(attribute)) {
                ids = bedsIndex.get(value);
            }

            // Perform intersection with previous results or initialize result set
            if (ids != null) {
                if (first) {
                    result = new HashSet<>(ids);
                    first = false;
                } else {
                    result.retainAll(ids);
                }
            }
        }

        if (result != null && !result.isEmpty()) {
            // Output the matching listings
            System.out.println("Matching listings:");
            for (String id : result) {
                JsonNode listing = listingMap.get(id);
                System.out.println(listing);
            }
        } else {
            System.out.println("No matching listings found.");
        }

        return result != null ? result : Collections.emptySet();
    }

    public void searchByUserInput(Input userInput) {
        Map<String, Object> filters = new HashMap<>();
        filters.put("baths", userInput.baths);
        filters.put("city", userInput.city);
        filters.put("price", userInput.price);
        filters.put("propertyType", userInput.typeOfProperty);
        filters.put("location", userInput.location);
        filters.put("beds", userInput.beds);

        Set<String> matchingIds = search(filters);
        System.out.println("Matching listing IDs: " + matchingIds);

    }

    public static void main(String[] args) {
        InvertedIndexing index = new InvertedIndexing();

        try {
            index.buildIndex("data.json");

            // Map<String, Object> filters = new HashMap<>();
            // filters.put("baths", "2");
            // filters.put("price", "15.5");
            // Set<String> matchingIds = index.search(filters);
            // System.out.println("Matching listing IDs: " + matchingIds);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
