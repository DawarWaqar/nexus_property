package com.acc.property;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

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

    public List<JsonNode> search(Map<String, String> filters) {
        List<JsonNode> resultList = new ArrayList<>();
        boolean first = true;

        if (filters.isEmpty()) {
            resultList.addAll(listingMap.values());
        } else {
            for (Map.Entry<String, String> entry : filters.entrySet()) {
                String attribute = entry.getKey();
                Object value = entry.getValue();
                Set<String> ids = null;

                // Retrieve IDs from the appropriate index based on the attribute
                if ("baths".equals(attribute)) {
                    ids = bathsIndex.getOrDefault(value, Collections.emptySet());
                } else if ("city".equals(attribute)) {
                    ids = cityIndex.getOrDefault(value, Collections.emptySet());
                } else if ("price".equals(attribute)) {
                    ids = priceIndex.getOrDefault(value, Collections.emptySet());
                } else if ("propertyType".equals(attribute)) {
                    ids = propertyTypeIndex.getOrDefault(value, Collections.emptySet());
                } else if ("location".equals(attribute)) {
                    ids = locationIndex.getOrDefault(value, Collections.emptySet());
                } else if ("beds".equals(attribute)) {
                    ids = bedsIndex.getOrDefault(value, Collections.emptySet());
                }

                if (ids != null && !ids.equals(Collections.emptySet())) {
                    if (first) {
                        resultList.addAll(ids.stream()
                                .map(id -> listingMap.get(id))
                                .collect(Collectors.toList()));
                        first = false;
                    } else {
                        resultList.retainAll(ids.stream()
                                .map(id -> listingMap.get(id))
                                .collect(Collectors.toList()));
                    }
                }

            }

        }

        if (!resultList.isEmpty()) {
            System.out.println("\nMatching listings (count: " + resultList.size() + "):\n");
            for (JsonNode listing : resultList) {
                System.out.println(listing + "\n");
            }
        } else {
            System.out.println("No matching listings found.");
        }

        return resultList;
    }

    public List<JsonNode> searchByUserInput(Input userInput) {

        Map<String, String> filters = new HashMap<>();

        if (userInput.baths != null && userInput.baths != "") {
            filters.put("baths", userInput.baths);
        }
        if (userInput.city != null && userInput.city != "") {
            filters.put("city", userInput.city);
        }
        if (userInput.price != null && userInput.price != "") {
            filters.put("price", userInput.price);
        }
        if (userInput.typeOfProperty != null && userInput.typeOfProperty != "") {
            filters.put("propertyType", userInput.typeOfProperty);
        }
        if (userInput.location != null && userInput.location != "") {
            filters.put("location", userInput.location);
        }
        if (userInput.beds != null && userInput.beds != "") {
            filters.put("beds", userInput.beds);
        }

        return search(filters);

    }

    public static void main(String[] args) {
        InvertedIndexing index = new InvertedIndexing();

        try {
            index.buildIndex("processedData.json");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
