package com.acc.property;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

public class PropertyRankingByUserPreference {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the number of beds you want: ");
        int userBeds = scanner.nextInt();
        System.out.print("Enter the number of baths you want: ");
        int userBaths = scanner.nextInt();

        String filePath = "data.json"; // Adjust the path to your JSON file
        try {
            List<PropertyListing> listings = loadListings(filePath);

            PriorityQueue<PropertyListing> queue = new PriorityQueue<>(
                (l1, l2) -> Integer.compare(l2.getRelevanceScore(), l1.getRelevanceScore())
            );

            for (PropertyListing listing : listings) {
                listing.setRelevanceScore(calculateRelevanceScore(listing, userBeds, userBaths));
                queue.add(listing);
            }

            while (!queue.isEmpty()) {
                PropertyListing listing = queue.poll();
                System.out.println("ID: " + listing.getId() + ", Beds: " + listing.getBeds() + ", Baths: " + listing.getBaths() + ", Relevance Score: " + listing.getRelevanceScore());
            }
        } catch (IOException e) {
            System.out.println("An error occurred while processing the file: " + e.getMessage());
        }
    }

    private static int calculateRelevanceScore(PropertyListing listing, int userBeds, int userBaths) {
        int score = 0;
        int beds = listing.extractNumber(listing.getBeds());
        int baths = listing.extractNumber(listing.getBaths());

        if (beds == userBeds) score++;
        if (baths == userBaths) score++;

        return score;
    }

    private static List<PropertyListing> loadListings(String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper.readValue(new File(filePath), new TypeReference<List<PropertyListing>>() {});
    }

    static class PropertyListing {
        private String beds;
        private String baths;
        private int id;
        private int relevanceScore; // Added field for sorting

        public int extractNumber(String text) {
            if (text == null || text.isEmpty() || text.equalsIgnoreCase("n/a")) return 0;
            String[] parts = text.split(" ");
            try {
                return Integer.parseInt(parts[0]);
            } catch (NumberFormatException e) {
                return 0;
            }
        }

        // Getters and setters
        public String getBeds() { return beds; }
        public String getBaths() { return baths; }
        public int getId() { return id; }
        public int getRelevanceScore() { return relevanceScore; }

        public void setBeds(String beds) { this.beds = beds; }
        public void setBaths(String baths) { this.baths = baths; }
        public void setId(int id) { this.id = id; }
        public void setRelevanceScore(int relevanceScore) { this.relevanceScore = relevanceScore; }
    }
}
