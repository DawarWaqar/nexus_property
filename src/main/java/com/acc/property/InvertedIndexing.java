package com.acc.property;

import java.io.*;
import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class InvertedIndexing {

    private Map<String, Set<String>> bathroomIndex;
    private Map<Double, Set<String>> lotSizeIndex;
    private Map<String, JsonNode> listingMap;

    public InvertedIndexing() {
        bathroomIndex = new HashMap<>();
        lotSizeIndex = new HashMap<>();
        listingMap = new HashMap<>();
    }

    public void buildIndex(String jsonFilePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File(jsonFilePath));

        for (JsonNode listing : root) {
            String id = listing.get("id").asText();
            int numBathrooms = listing.get("bathrooms").asInt();
            double lotSize = listing.get("lot_size").asDouble();

            // Build indexes for each attribute
            bathroomIndex.computeIfAbsent(Integer.toString(numBathrooms), k -> new HashSet<>()).add(id);
            lotSizeIndex.computeIfAbsent(lotSize, k -> new HashSet<>()).add(id);

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
            if ("bathrooms".equals(attribute)) {
                ids = bathroomIndex.get(value.toString());
            } else if ("lot_size".equals(attribute)) {
                ids = lotSizeIndex.get(value);
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

    public static void main(String[] args) {
        InvertedIndexing index = new InvertedIndexing();

        try {
            index.buildIndex("data.json");

            Map<String, Object> filters = new HashMap<>();
            // filters.put("bathrooms", numBathrooms);
            // filters.put("lot_size", lotSize);
            Set<String> matchingIds = index.search(filters);
            System.out.println("Matching listing IDs: " + matchingIds);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
