package com.acc.property;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;

public class FrequencyCount {
	public static void main(String[] args, List<JsonNode> jsonArray, String searchString) {
		try {
			if (jsonArray == null) {
				System.out.println("No data was filtered!");
				return;
			}

			// Creating a HashMap to store string frequencies
			HashMap<String, Integer> frequencyMap = new HashMap<>();

			// Iterating through each string in the JSON data
			for (JsonNode listing : jsonArray) {
				listing.fields().forEachRemaining(entry -> {
					String[] str = entry.getValue().toString().toLowerCase().replace("\"", "").split("[\\s,]+");
					for (String s: str) {
					// Incrementing the frequency count of the string
					frequencyMap.put(s, frequencyMap.getOrDefault(s, 0) + 1);
					}
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
