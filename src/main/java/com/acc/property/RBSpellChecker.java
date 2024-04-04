package com.acc.property;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.*;

public class RBSpellChecker {

    // TreeSet to store unique words in a case-insensitive manner
    private TreeSet<String> rbDict;

    // Constructor initializes the TreeSet for storing words
    public RBSpellChecker() {
        rbDict = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    }

    // Method to append a word to the dictionary
    public void appendEntry(String word) {
        // Add the word to the TreeSet if it is not already present
        if (!rbDict.contains(word)) {
            rbDict.add(word);
        }
    }

    // Method to calculate Levenshtein distance between two words
    public static int calculateLevenshteinDistance(String firstWord, String secondWord) {
        // Initialize a matrix to store distances
        int[][] matrix = new int[firstWord.length() + 1][secondWord.length() + 1];

        // Fill the first row and column with increasing numbers
        for (int i = 0; i <= firstWord.length(); i++) {
            matrix[i][0] = i;
        }
        for (int j = 0; j <= secondWord.length(); j++) {
            matrix[0][j] = j;
        }

        // Calculate distances based on dynamic programming approach
        for (int i = 1; i <= firstWord.length(); i++) {
            for (int j = 1; j <= secondWord.length(); j++) {
                int cost = (firstWord.charAt(i - 1) == secondWord.charAt(j - 1)) ? 0 : 1;
                matrix[i][j] = Math.min(Math.min(matrix[i - 1][j] + 1, matrix[i][j - 1] + 1),
                        matrix[i - 1][j - 1] + cost);
            }
        }
        // Return the Levenshtein distance
        return matrix[firstWord.length()][secondWord.length()];
    }

    // Method to find similar words to a given input word
    public void findSimilarWords(String inputWord, int K) {
        // Priority queue to store similar words based on their distances
        PriorityQueue<Map.Entry<String, Integer>> similarWordsQueue = new PriorityQueue<>(K,
                Map.Entry.comparingByValue());

        // Iterate through the dictionary and calculate distances
        for (String word : rbDict) {
            int distance = calculateLevenshteinDistance(inputWord, word);
            similarWordsQueue.offer(new AbstractMap.SimpleEntry<>(word, distance));
        }

        // Array to store K most similar words
        String[] similarWordsArr = new String[K];
        Arrays.fill(similarWordsArr, "");

        // Retrieve K most similar words from the priority queue
        int count = 0;
        while (!similarWordsQueue.isEmpty() && count < K) {
            similarWordsArr[count] = similarWordsQueue.poll().getKey();
            count++;
        }

        // Print the suggestions
        System.out.print("Did you mean? ");
        for (String similarWord : similarWordsArr) {
            if (!similarWord.isEmpty()) {
                System.out.print(similarWord + " ");
            }
        }
        System.out.println();
    }

    // Method to populate the dictionary from a file
    public void populate(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String word = line.trim(); // Remove leading and trailing whitespace
                appendEntry(word);
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle file reading errors
        }
    }

    // Method to split a string into words
    public static List<String> splitWords(String input) {
        return Arrays.asList(input.split("\\s+"));
    }

    // Method to check if a string contains an integer
    public boolean containsInteger(String str) {
        Pattern pattern = Pattern.compile("-?\\d+");
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }

    // Method to suggest words for misspelled words in an input string
    public boolean suggestWords(String input) {
        List<String> wordsList = splitWords(input);
        List<String> incorrectWords = new ArrayList<>();

        // Iterate through the words in the input
        for (String word : wordsList) {
            word = word.replaceAll("^[.,]+", "").replaceAll("[.,]+$", "");
            word = word.toLowerCase(); // Convert word to lowercase for case-insensitive comparison

            // Skip the word if it contains an integer
            if (containsInteger(word))
                continue;

            // Check if the word is not in the dictionary
            if (!rbDict.contains(word)) {
                System.err.println("Wrong spelling for: " + word);
                findSimilarWords(word, 5); // Find similar words for misspelled word
                incorrectWords.add(word);
                System.out.println();
            }
        }

        // Return true if there are incorrect words, false otherwise
        return !incorrectWords.isEmpty();
    }

    // Main method to demonstrate the spell checker
    public static void main(String[] args) {
        RBSpellChecker spellChecker = new RBSpellChecker();
        spellChecker.populate("english-dictionary-source-UMich.txt");
    }
}
