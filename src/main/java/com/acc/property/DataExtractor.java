package com.acc.property;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataExtractor {

    public static String fetchFirstNumber(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group();
        } else {
            return "";
        }
    }

    public static String fetchPrice(String input) {
        // Regular expression to match the price pattern
        Pattern pattern = Pattern.compile("\\$([0-9,]+)");

        // Matcher to find the price pattern in the input string
        Matcher matcher = pattern.matcher(input);

        // Check if the price pattern is found
        if (matcher.find()) {
            // Extract the matched group (price with dollar sign and commas)
            String matchedPrice = matcher.group(1);

            // Remove commas from the matched group
            String priceWithoutCommas = matchedPrice.replaceAll(",", "");

            // Return the price without the dollar sign and commas
            return priceWithoutCommas;
        } else {
            // If no price pattern is found, return an error message
            return "Price not found.";
        }
    }

    public static String patternMatch(String input, String choice) {
        String output = "";

        switch (choice) {
            case "fetchFirstNumber":
                output = fetchFirstNumber(input);
                break;
            case "price":
                output = fetchPrice(input);
                break;
            default:
                System.out.println("Invalid input for pattern matching");
                break;
        }

        return output;

    }

    public static void main(String[] args) {

    }
}