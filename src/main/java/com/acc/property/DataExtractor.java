package com.acc.property;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataExtractor {

    private static final String ADDRESS_DATA = "1622 CYPRESS, Windsor, Ontario";
    private static final String PRICE_DATA = "\"price\": \"$1,749,000\"";

    public static String fetchFirstNumber(String input) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return matcher.group();
        } else {
            return null;
        }
    }

    public static String fetchPrice(String input) {
        Pattern pattern = Pattern.compile("\"price\":\\s*\"\\$(.*?)\"");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            // Remove commas from the matched group before returning
            return matcher.group(1).replace(",", "");
        } else {
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
        String price = fetchPrice("$4,000,000");
        System.out.println(price);

    }
}