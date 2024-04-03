package com.acc.property;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class PropertyFileReader {
    public static ArrayList<String> main(String parameter, String fileName) {
    	ArrayList<String> outputParameter = new ArrayList<>();
        try {
            // Create a JSON parser
            JSONParser parser = new JSONParser();

            // Parse the JSON file
            Object obj = parser.parse(new FileReader(fileName));

            // Cast the parsed object to JSONArray
            JSONArray jsonArray = (JSONArray) obj;

            // Iterate over each JSON object in the array
            for (Object o : jsonArray) {
                // Cast each object to JSONObject
                JSONObject jsonObject = (JSONObject) o;

                // Access properties of each JSON object
                String property = (String) jsonObject.get(parameter);

                // Do something with the properties
                outputParameter.add(property);
            }
            return outputParameter;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return outputParameter;
        }
    }
}
