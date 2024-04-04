package com.acc.property;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import com.acc.property.FrequencyCount;
import com.fasterxml.jackson.databind.JsonNode;

class Input {

	String location;
	String price;
	String beds;
	String baths;
	String typeOfProperty;
	String city;
	private static final String numBedrooms = "beds";
	private static final String numBathrooms = "baths";
	private static final String propertyType = "propertyType"; // in dollars per month
	private static final String downPayment = "price"; // in dollars
	private static final String email = "email";
	private static final String locationStr = "location";
	private static final String password = "password";
	private static final String cityStr = "city";
	private static final String freqCount = "freqencyCount";
	private static final List<String> validWordCompletionParameters = new ArrayList<>(Arrays.asList(locationStr, cityStr));

	private static Scanner sc = new Scanner(System.in);

	public static String takeInput(String dataType, String dataString, String fileName) {
		DataValidation d = new DataValidation();
		String data = "";
		boolean dataValid = false;

		// validating input based on regular expression
		while (!dataValid) {
			System.out.print(dataString + ": ");
			String s = sc.nextLine();
			if (d.isValid(dataType, s)) {
				dataValid = true;
				data = s;
			} else {
				dataValid = false;
				System.out.println(dataString + " is not valid");
			}
		}

		return data;
	}

	public static void searchWordCompletion(String dataType, String data, String dataString, String fileName) {
		// building array of values of parameter from the output file for word
		// completion
		ArrayList<String> parameterValues = PropertyFileReader.main(dataType, fileName);
		// get all words starting with input string
		List<String> suggestedWords = WordCompletion.main(parameterValues, data);

		// if there are words starting with input string
		if (!suggestedWords.isEmpty()) {
			System.out.println("Did you mean?: ");

			// List all suggested words to user
			for (String suggestedWord : suggestedWords) {
				System.out.println(suggestedWord);
			}
			// take input again with valid value
			if (getDecision())
				takeInput(dataType, dataString, fileName);
		}
	}

	public static boolean getDecision() {
		System.out.print("Do you want to update input (y/n): ");
		String decision = sc.nextLine();
		if (decision.toLowerCase().equals("y")) {
			return true;
		} else if (decision.toLowerCase().equals("n")) {
			return false;
		} else {
			System.out.println("Invalid Input!");
			return getDecision();
		}
	}

	public static Input main(String[] args, String fileName) {
		System.out.println(
				"Search for a property based on below given parameters (please leave the parameters input empty if it's not decided.): ");
		Scanner sc = new Scanner(System.in);
		Input input = new Input();

		// taking inputs one by one
		input.price = takeInput(downPayment, "Price", fileName);
		input.beds = takeInput(numBedrooms, "Number of Bedrooms", fileName);
		input.baths = takeInput(numBathrooms, "Number of Bathrooms", fileName);
		input.typeOfProperty = takeInput(propertyType, "Property Type", fileName);
		input.city = takeInput(cityStr, "City", fileName);
		input.location = takeInput(locationStr, "Location", fileName);

		// spell-checker
		RBSpellChecker spellChecker = new RBSpellChecker();
		spellChecker.populate("english-dictionary-source-UMich.txt");

		Boolean isIncorrectWord = spellChecker.suggestWords(input.location);

		while (isIncorrectWord) {
			if (getDecision()) {
				input.location = takeInput(locationStr, "Location", fileName);
				isIncorrectWord = spellChecker.suggestWords(input.location);
			} else {
				isIncorrectWord = false;
			}
		}
		// spell-checker end

		// Word completion if data validation is successfull
		if (validWordCompletionParameters.contains("location") && input.location.length() != 0)
			searchWordCompletion(locationStr, input.location, "Location", fileName);
		if (validWordCompletionParameters.contains("city") && input.city.length() != 0)
			searchWordCompletion(cityStr, input.city, "City", fileName);

		// word-completion ended
		System.out.println("Start with the Search of a keyword: ");
		boolean isContinue = true;
		while (isContinue) {
			String searchString = takeInput(freqCount, "Input word for frequency count", fileName);
//			sc.nextLine().toLowerCase(); // Convert to lowercase for case-insensitive search
			JsonNode jsonObj = null;
			FrequencyCount.main(args, jsonObj, searchString);
			SearchFreq.main(args, searchString);
			isContinue = getDecision();
		}

		sc.close();
		return input;
	}
}
