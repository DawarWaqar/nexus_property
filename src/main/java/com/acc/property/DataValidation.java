package com.acc.property;
import java.util.regex.Pattern;

public class DataValidation {
	private static final String numBedrooms = "beds";
	private static final String numBathrooms = "baths";
	private static final String propertyType = "propertyType"; // in dollars per month
	private static final String downPayment = "price"; // in dollars
	private static final String email = "email";
	private static final String location = "location";
	private static final String password = "password";

	// Regular expressions for validation
	private static final String LOCATION_REGEX = "^[0-9]*[a-zA-Z\\s.,'-]+$|^$";
	private static final String POSITIVE_INTEGER_REGEX = "^[1-9]\\d*$|^$";
	private static final String CURRENCY_REGEX = "^[1-9]\\d*(\\.\\d+)?$|^$";
	private static final String PROPERTY_TYPE = "(Residential|Commercial|Land|Special Purpose|Mixed-Use|Other)|^$";
	private static final String EMAIL_REGEX = "^[\\w!#$%&'*+/=?^`{|}~-]{5,}(?:\\.[\\w!#$%&'*+/=?^`{|}~-]+)*@(?:[a-zA-Z0-9]{4,}\\.)+[a-zA-Z]{2,6}$|^$";
	private static final String PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[@#$%^&*!])(?=.{8,}$)|^$";
//  private static final String LOCATION_REGEX = "^(?=.*\\S)[\\w\\s.,'-]+,\\s*[\\w\\s.,'-]+,\\s*[\\w\\s.,'-]+,\\s*[\\w\\s.,'-]+[^\\s]$";
//  private static final String SIZE_REGEX = "^[1-9]\\d*(\\.\\d+)?$";
//  private static final String YEAR_BUILT_REGEX = "^\\d{4}$";
//  private static final String COORDINATES_REGEX = "^-?\\d{1,3}\\.\\d{4,},\\s*-?\\d{1,3}\\.\\d{4,}$";

	// Validate email
	public static boolean validateEmail(String email) {
		return Pattern.matches(EMAIL_REGEX, email);
	}

	// Validate password
	public static boolean validatePassword(String password) {
		return Pattern.matches(PASSWORD_REGEX, password);
	}

	// Validate property location
	public static boolean validateLocation(String location) {
		return Pattern.matches(LOCATION_REGEX, location);
	}

	// Validate positive integer values (e.g., number of bedrooms, bathrooms)
	public static boolean validatePositiveInteger(String value) {
		return Pattern.matches(POSITIVE_INTEGER_REGEX, value);
	}

	// Validate currency values (e.g., property sale prices, rental prices, loan
	// terms, down payment amount)
	public static boolean validateCurrency(String value) {
		return Pattern.matches(CURRENCY_REGEX, value);
	}

	public static boolean isValidPropertyType(String value) {
		return Pattern.matches(PROPERTY_TYPE, value);
	}

	public boolean isValid(String strType, String str) {
		switch (strType) {
		case location:
			return validateLocation(str);
		case numBedrooms:
			return validatePositiveInteger(str);
		case numBathrooms:
			return validatePositiveInteger(str);
		case propertyType:
			return isValidPropertyType(str);
		case downPayment:
			return validateCurrency(str);
		case email:
			return validateEmail(str);
		case password:
			return validatePassword(str);
		default:
			return false;
		}
	}
}