package com.acc.property;

import com.acc.property.DataValidation;
import com.acc.property.PropertyFileReader;
import com.acc.property.WordCompletion;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Input {

	String location;
	String price;
	String beds;
	String baths;
	String typeOfProperty;
	private static final String numBedrooms = "beds";
	private static final String numBathrooms = "baths";
	private static final String propertyType = "propertyType"; // in dollars per month
	private static final String downPayment = "price"; // in dollars
	private static final String email = "email";
	private static final String locationStr = "location";
	private static final String password = "password";
	private static final List<String> validWordCompletionParameters = new ArrayList<>(Arrays.asList(locationStr));

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

		// Word completion if data validation is successfull
		if (validWordCompletionParameters.contains(dataType) && data.length() != 0)
			searchWordCompletion(dataType, data, dataString, fileName);

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
		System.out.print("Do you want to update the input (y/n): ");
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

	public static Input main(String fileName) {
		System.out.println(
				"Search for a property based on below given parameters (please leave the parameters input empty if it's not decided.): ");
		Scanner sc = new Scanner(System.in);
		Input input = new Input();

		// taking inputs one by one
		input.location = takeInput(locationStr, "Location", fileName);
		input.price = takeInput(downPayment, "Price", fileName);
		input.beds = takeInput(numBedrooms, "Number of Bedrooms", fileName);
		input.baths = takeInput(numBathrooms, "Number of Bathrooms", fileName);
		input.typeOfProperty = takeInput(propertyType, "Property Type", fileName);

		sc.close();
		return input;
	}
}
