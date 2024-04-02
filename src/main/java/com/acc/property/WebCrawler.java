package com.acc.property;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.time.Duration;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WebCrawler {
	private static WebDriver driver = null;
	static JSONArray jsonArray = new JSONArray();
	static WebDriverWait wait;

	public static void main(String[] args) {
		setupWebDriver();
		startCrawling();
	}

	public static void setupWebDriver() {
		ChromeOptions chromeOptions = new ChromeOptions();
		String user_agent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.110 Safari/537.36";
		chromeOptions.addArguments("user-agent=" + user_agent);
		driver = new ChromeDriver(chromeOptions);
		driver.manage().window().maximize();
		wait = new WebDriverWait(driver, Duration.ofSeconds(30));
	}

	public static void startCrawling() {
		try {
			String[] websites = { "point2homes", "remax" };
			for (String website : websites) {
				String url = constructURL(website);

				driver.get(url);
				Thread.sleep(5000); // Adding a wait for page load (adjust if necessary)

				switch (website) {
					case "point2homes":
						Crawlp2h();
						break;
					case "remax":
						CrawlRemax(url, "on/windsor");
						break;
				}
			}

			// Write JSON array to file
			try (FileWriter file = new FileWriter("data.json")) {
				file.write(jsonArray.toJSONString());
				System.out.println("JSON array written to data.json file");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			driver.quit();
		}
	}

	private static String constructURL(String website) {
		switch (website) {
			case "point2homes":
				return String.format("https://www.point2homes.com/CA/Real-Estate-Listings/ON/Windsor.html");
			case "remax":
				return String.format("https://www.remax.ca/");
			default:
				throw new IllegalArgumentException("Unsupported website: " + website);
		}
	}

	public static void Crawlp2h() {
		try {
			// Wait for the listings to load
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("listings")));
			List<WebElement> items = driver.findElements(By.cssSelector(".items > li"));

			for (WebElement item : items) {
				// System.out.println(item.getText());
				JSONObject itemObject = new JSONObject();
				try {

					String dataAddressValue = GetaddressValue(item);
					itemObject.put("location", dataAddressValue);

					String bedValue = GetBedsValue(item);
					itemObject.put("beds", bedValue);

					String bathsValue = GetBathsValue(item);
					itemObject.put("baths", bathsValue);

					String propertyType = GetPropertyTypeValue(item);
					itemObject.put("propertyType", propertyType);

					String priceValue = GetPriceValue(item);
					itemObject.put("price", priceValue);

					// Find the <div> element with the specified class name
					WebElement openHouseElement = item.findElement(By.cssSelector("div.open-house-right"));
					// Find the nested <div> element with the specified class name containing open
					// house information
					WebElement openHouseInfoElement = openHouseElement
							.findElement(By.cssSelector("div.open-house-line"));
					String[] DayDateTime = GetDayDateTime(openHouseInfoElement);
					// Output the extracted components
					itemObject.put("OpenHouseDay: ", DayDateTime[0]);
					itemObject.put("OpenHouseDate: ", DayDateTime[1]);
					itemObject.put("OpenHouseMonth: ", DayDateTime[2]);
					itemObject.put("OpenHouseStartTime: ", DayDateTime[4]);
					itemObject.put("OpenHouseEndTime: ", DayDateTime[6]);

					jsonArray.add(itemObject);

				} catch (org.openqa.selenium.NoSuchElementException e) {
					jsonArray.add(itemObject);
				}
			}
			// // Write JSON array to file
			// try (FileWriter file = new FileWriter("output.json", true)) {
			// file.write(jsonArrayP2home.toJSONString());
			// System.out.println("JSON array written to output.json file");
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
		} finally {
			// driver.quit();
		}
	}

	public static String GetaddressValue(WebElement item) {
		WebElement addressContainer = item.findElement(By.className("address-container"));
		// Get the value of the data-address attribute
		String addressValue = addressContainer.getAttribute("data-address");

		// Print the extracted value
		// System.out.println("Value of data-address attribute: " + addressValue);

		return addressValue;

	}

	public static String GetBedsValue(WebElement item) {
		WebElement bedElement = item.findElement(By.cssSelector("li.ic-beds"));
		String bedsValue = bedElement.getText();

		// Print the extracted value
		// System.out.println("Bed Value: " + bedsValue);
		return bedsValue;
	}

	public static String GetBathsValue(WebElement item) {
		// Find the <li> element with the specified class name
		WebElement bathElement = item.findElement(By.cssSelector("li.ic-baths"));

		// Extract the text content of the <li> element
		String bathsValue = bathElement.getText().trim();

		// Print the extracted value
		// System.out.println("Baths value: " + bathsValue);

		return bathsValue;
	}

	public static String GetPropertyTypeValue(WebElement item) {

		// Find the <li> element with the specified class name
		WebElement porpertyTypeElement = item.findElement(By.cssSelector("li.property-type.ic-proptype"));

		// Extract the text content of the <li> element
		String propertyType = porpertyTypeElement.getText().trim();

		// Print the extracted value
		// System.out.println("Property type: " + propertyType);

		return propertyType;
	}

	public static String GetPriceValue(WebElement item) {
		// Find the <div> element with the specified class name
		WebElement priceElement = item.findElement(By.cssSelector("div.price"));

		// Extract the value of the "data-price" attribute
		String price = priceElement.getAttribute("data-price");

		// Print the extracted value
		// System.out.println("Price: " + price);

		return price;
	}

	public static String[] GetDayDateTime(WebElement item) {
		// Extract the text from the element
		String openHouseText = item.getText();

		// Split the text to extract individual components
		String[] parts = openHouseText.split(" ");

		// Extracting individual components
		String day = parts[0].replace(",", ""); // Remove the comma
		String date = parts[1];
		String month = parts[2];
		String startTime = parts[3];
		String endTime = parts[6];

		// // Output the extracted components
		// System.out.println("Day: " + day);
		// System.out.println("Date: " + date);
		// System.out.println("Month: " + month);
		// System.out.println("Start Time: " + startTime);
		// System.out.println("End Time: " + endTime);

		return parts;
	}

	// ************************************ ZooCasa
	// ************************************
	public static void CrawlRemax(String defaultUrl, String location) {

		try {
			for (int i = 0; i < 4; i++) {
				String Url = String.format("%s/%s-real-estate?pageNumber=%s", defaultUrl, location, i + 1);
				System.out.println(Url);
				driver.get(Url);
				Thread.sleep(2000);
				List<WebElement> listingCards = driver.findElements(By.cssSelector("[data-testid='listing-card']"));
				// Loop through each listing card and print its details
				for (WebElement listingCard : listingCards) {
					JSONObject itemObject = new JSONObject();
					// Get the price
					WebElement priceElement = listingCard.findElement(By.cssSelector(".listing-card_price__lEBmo"));
					String price = priceElement.getText();

					// Get the bed and bath details
					WebElement bedElement = listingCard.findElement(By.cssSelector("[data-cy='property-beds']"));
					WebElement bathElement = listingCard.findElement(By.cssSelector("[data-cy='property-baths']"));
					String bed = bedElement.getText();
					String bath = bathElement.getText();

					// Get the address
					WebElement addressElement = listingCard.findElement(By.cssSelector("[data-cy='property-address']"));
					String address = addressElement.getText();

					itemObject.put("location", address);
					itemObject.put("beds", bed);
					itemObject.put("baths", bath);
					itemObject.put("propertyType", "residential");
					itemObject.put("price", price);

					// Print the details
					System.out.println("Price: " + price);
					System.out.println("Bed: " + bed);
					System.out.println("Bath: " + bath);
					System.out.println("Location: " + address);
					System.out.println("------------------------------");

					jsonArray.add(itemObject);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			driver.quit();
		}
	}
}