package com.acc.property;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.*;

public class RBSpellChecker {

    private TreeSet<String> rbDict;

    public RBSpellChecker() {
        rbDict = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    }

    public void appendEntry(String word) {

        if (!rbDict.contains(word)) {
            rbDict.add(word);
        }
    }

    public static int calculateLevenshteinDistance(String firstWord, String secondWord) {
        int[][] matrix = new int[firstWord.length() + 1][secondWord.length() + 1];

        for (int i = 0; i <= firstWord.length(); i++) {
            matrix[i][0] = i;
        }
        for (int j = 0; j <= secondWord.length(); j++) {
            matrix[0][j] = j;
        }

        for (int i = 1; i <= firstWord.length(); i++) {
            for (int j = 1; j <= secondWord.length(); j++) {
                int cost = (firstWord.charAt(i - 1) == secondWord.charAt(j - 1)) ? 0 : 1;
                matrix[i][j] = Math.min(Math.min(matrix[i - 1][j] + 1, matrix[i][j - 1] + 1),
                        matrix[i - 1][j - 1] + cost);
            }
        }
        return matrix[firstWord.length()][secondWord.length()];
    }

    public void findSimilarWords(String inputWord, int K) {
        PriorityQueue<Map.Entry<String, Integer>> similarWordsQueue = new PriorityQueue<>(K,
                Map.Entry.comparingByValue());

        for (String word : rbDict) {
            int distance = calculateLevenshteinDistance(inputWord, word);
            similarWordsQueue.offer(new AbstractMap.SimpleEntry<>(word, distance));
        }

        String[] similarWordsArr = new String[K];
        Arrays.fill(similarWordsArr, "");

        int count = 0;
        while (!similarWordsQueue.isEmpty() && count < K) {
            similarWordsArr[count] = similarWordsQueue.poll().getKey();
            count++;
        }

        System.out.print("Did you mean? ");
        for (String similarWord : similarWordsArr) {
            if (!similarWord.isEmpty()) {
                System.out.print(similarWord + " ");
            }
        }
        System.out.println();
    }

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

    public static List<String> splitWords(String input) {
        return Arrays.asList(input.split("\\s+"));
    }

    public boolean containsInteger(String str) {
        Pattern pattern = Pattern.compile("-?\\d+");
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }

    public boolean suggestWords(String input) {
        List<String> wordsList = splitWords(input);
        List<String> incorrectWords = new ArrayList<>();
        System.out.println("");

        for (String word : wordsList) {
            word = word.toLowerCase();
            if (containsInteger(word))
                continue;

            if (!rbDict.contains(word)) {
                System.err.println("Wrong spelling for: " + word);
                findSimilarWords(word, 5);
                incorrectWords.add(word);
                System.out.println();
            }
        }

        return incorrectWords.size() > 0;
    }

    public static void main(String[] args) {
        RBSpellChecker spellChecker = new RBSpellChecker();
        spellChecker.populate("english-dictionary-source-UMich.txt");

    }
}
