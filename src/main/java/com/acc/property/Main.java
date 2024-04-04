package com.acc.property;

import java.util.List;
import java.util.Scanner;
import com.fasterxml.jackson.databind.JsonNode;

public class Main {
	private static final String freqCount = "freqencyCount";

    public static void runWebCrawler(String[] args) {

        WebCrawler.main(args); // running the webcrawler, user should have the argument==1 if it wants to run
                               // with
                               // webcrawler
    }

    public static void main(String[] args) {
        try {
        	// spell-checker
    		RBSpellChecker spellChecker = new RBSpellChecker();
    		spellChecker.populate("english-dictionary-source-UMich.txt");
    		
    		Scanner sc = new Scanner(System.in);
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
			Input input = I.main(args, fileName, spellChecker);

            // inverted-indexing
			InvertedIndexing index = new InvertedIndexing();
            index.buildIndex("data.json");
            List<JsonNode> filteredResults = index.searchByUserInput(input);
            
            System.out.println("Start with the Search of a keyword: ");
    		boolean isContinue = true;
    		while (isContinue) {
    			String searchString = Input.takeInput(freqCount, "Input word for frequency count", fileName, spellChecker);
//    			sc.nextLine().toLowerCase(); // Convert to lowercase for case-insensitive search
    			JsonNode jsonObj = null;
    			FrequencyCount.main(args, jsonObj, searchString);
    			SearchFreq.main(args, searchString);
    			isContinue = Input.getDecision();
    		}
            
            PropertyRankingByUserPreference.main(args);

			sc.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}