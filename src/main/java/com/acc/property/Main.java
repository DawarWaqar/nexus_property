package com.acc.property;

import java.util.List;
import java.util.Scanner;
import com.fasterxml.jackson.databind.JsonNode;

public class Main {

    public static void runWebCrawler(String[] args) {

        WebCrawler.main(args); // running the webcrawler, user should have the argument==1 if it wants to run
                               // with
                               // webcrawler
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.println("Do you want to run web crawling? (y/n):");
            String isWebCrawling = sc.nextLine();
            if (isWebCrawling.toLowerCase().equals("y")) {
                runWebCrawler(args);
                PostProcessData.main(args);

            } else if (isWebCrawling.toLowerCase().equals("n")) {
                System.out.println("The program will run without web crawler now.");
            } else {
                System.out.println("Invalid Input");
                return;
            }

            String fileName = "data.json";
            Input I = new Input();
            Input input = I.main(args, fileName);

            sc.close();

            // inverted-indexing
            InvertedIndexing index = new InvertedIndexing();
            index.buildIndex("data.json");
            List<JsonNode> filteredResults = index.searchByUserInput(input); // for aashma, you are very welcome:)

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}