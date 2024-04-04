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
    	System.out.println("\r\n"
    			+ " _        _______                    _______     _______  _______  _______ \r\n"
    			+ "( (    /|(  ____ \\|\\     /||\\     /|(  ____ \\   (  ____ \\(  ___  )(       )\r\n"
    			+ "|  \\  ( || (    \\/( \\   / )| )   ( || (    \\/   | (    \\/| (   ) || () () |\r\n"
    			+ "|   \\ | || (__     \\ (_) / | |   | || (_____    | |      | |   | || || || |\r\n"
    			+ "| (\\ \\) ||  __)     ) _ (  | |   | |(_____  )   | |      | |   | || |(_)| |\r\n"
    			+ "| | \\   || (       / ( ) \\ | |   | |      ) |   | |      | |   | || |   | |\r\n"
    			+ "| )  \\  || (____/\\( /   \\ )| (___) |/\\____) | _ | (____/\\| (___) || )   ( |\r\n"
    			+ "|/    )_)(_______/|/     \\|(_______)\\_______)(_)(_______/(_______)|/     \\|\r\n"
    			+ "                                                                           \r\n"
    			+ "");
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
			System.out.println();
			Input input = I.main(args, fileName, spellChecker);

            // inverted-indexing
			InvertedIndexing index = new InvertedIndexing();
            index.buildIndex("data.json");
            List<JsonNode> filteredResults = index.searchByUserInput(input);
            
            // search frequency and frequency count  
            if (filteredResults.size() != 0) {
    		System.out.println();
    		System.out.println("Start with the Search of a keyword: ");
    		boolean isContinue = true;
    		while (isContinue) {    
    			String searchString = Input.takeInput(freqCount, "Input word for frequency count", fileName, spellChecker);
    			FrequencyCount.main(args, filteredResults, searchString);
    			SearchFreq.main(args, searchString);
    			isContinue = Input.getDecision(freqCount);
    		}
            }
    		// end of search frequency and frequency count
    		
            System.out.println();
    		System.out.println("Steps for page ranking will be calculated based on number of bads and baths in a property: ");
            PropertyRankingByUserPreference.main(args);

			sc.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}