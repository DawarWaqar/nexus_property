package com.acc.property;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

public class PostProcessData {

    public static void processProperties(String filePath) throws IOException {

        try {
            // Step 1: Read the JSON file and parse it into a JsonNode
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(new File(filePath));

            // Step 2: Generate IDs and remove empty objects
            int idCounter = 1;
            Iterator<JsonNode> iterator = rootNode.elements();
            while (iterator.hasNext()) {
                JsonNode propertyNode = iterator.next();
                if (propertyNode.size() == 0) {
                    iterator.remove(); // Remove empty object
                } else {
                    // Append ID to non-empty object
                    ((ObjectNode) propertyNode).put("id", idCounter++);
                }
            }

            // Step 3: Write the modified JsonNode back to the same JSON file
            objectMapper.writeValue(new File(filePath), rootNode);

            System.out.println("Post processing completeted");
        } catch (FileNotFoundException e) {
            System.err.println("Error: The JSON file does not exist.");
            System.exit(1); // Exit with error code 1
        } catch (IOException e) {
            System.err.println("Error processing JSON file.");
            System.exit(1); // Exit with error code 1
        }

    }

    public static void main(String[] args) throws IOException {
        processProperties("data.json");
    }
}
