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
	private static final String locationStr = "location";
	private static final String cityStr = "city";
	private static final List<String> validWordCompletionParameters = new ArrayList<>(
			Arrays.asList(locationStr, cityStr));
	private static final Scanner sc = new Scanner(System.in);

	public static String takeInput(String dataType, String dataString, String fileName, RBSpellChecker spellChecker) {
		
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

		Boolean isIncorrectWord = spellChecker.suggestWords(data);

		//spell-checker starts
		while (isIncorrectWord) {
			if (getDecision()) {
				data = takeInput(locationStr, "Location", fileName, spellChecker);
//				isIncorrectWord = spellChecker.suggestWords(data);
			} else {
				isIncorrectWord = false;
			}
		}
		// spell-checker end

		// Word completion if data validation is successfull
		if (validWordCompletionParameters.contains(dataType) && data.length() != 0)
			data = searchWordCompletion(dataType, data, dataString, fileName, spellChecker);
		// word-completion ended
		return data.toLowerCase();
	}

	public static String searchWordCompletion(String dataType, String data, String dataString, String fileName,
			RBSpellChecker spellChecker) {
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
				return takeInput(dataType, dataString, fileName, spellChecker);
			else 
				return data;
		}
		return data;
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

	public static Input main(String[] args, String fileName, RBSpellChecker spellChecker) {
		System.out.println(
				"Search for a property based on below given parameters (please leave the parameters input empty if it's not decided.): ");
		Input input = new Input();


		// taking inputs one by one
		input.price = takeInput(downPayment, "Price", fileName, spellChecker);
		input.beds = takeInput(numBedrooms, "Number of Bedrooms", fileName, spellChecker);
		input.baths = takeInput(numBathrooms, "Number of Bathrooms", fileName, spellChecker);
		input.typeOfProperty = takeInput(propertyType, "Property Type", fileName, spellChecker);
		input.city = takeInput(cityStr, "City", fileName, spellChecker);
		input.location = takeInput(locationStr, "Location", fileName, spellChecker);
//		sc.close();
		return input;
	}
}
