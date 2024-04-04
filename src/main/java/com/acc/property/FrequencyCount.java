package com.acc.property;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;

public class FrequencyCount {
	public static void main(String[] args, JsonNode jsonArray, String searchString) {
//    	ArrayList<String> parameters = new ArrayList<>(Arrays.asList());
		// Read data from JSON file
		try {
//        ObjectMapper mapper = new ObjectMapper();
//        JsonNode jsonArray = mapper.readTree(new File("data.json"));

			if (jsonArray == null) {
				System.out.println("No data was filtered!");
				return;
			}

			// Creating a HashMap to store string frequencies
			HashMap<String, Integer> frequencyMap = new HashMap<>();

			// Iterating through each string in the JSON data
			for (JsonNode listing : jsonArray) {
				listing.fields().forEachRemaining(entry -> {
					String str = entry.getValue().toString().toLowerCase().replace("\"", "");
					// Incrementing the frequency count of the string
					frequencyMap.put(str, frequencyMap.getOrDefault(str, 0) + 1);
				});

			}

			// Displaying the frequency count of the search string
			int count = frequencyMap.getOrDefault(searchString, 0);
			System.out.println("Frequency count of '" + searchString + "': " + count);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

}
