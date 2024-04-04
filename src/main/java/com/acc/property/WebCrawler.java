package com.acc.property;

import java.awt.Dimension;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.time.Duration;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
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
			String[] websites = { "point2homes", "remax", "propertyguys" };
			for (String website : websites) {
				String url = constructURL(website);
				Thread.sleep(2000); // Adding a wait for page load
				switch (website) {
					case "point2homes":
						Crawlp2h(url, "on");
						break;
					case "remax":
						CrawlRemax(url, "on");
						break;
					case "propertyguys":
						CrawlPropertyGuys(url, "on");
						break;
				}
			}

			// Write JSON array to file
			try (FileWriter file = new FileWriter("data.json")) {
				file.write(jsonArray.toJSONString());
				System.out.println("JSON array written to data.json file");
			} catch (IOException e) {
				System.out.println("Unable to write in the file! Please try again.");
			}
		} catch (Exception e) {
			System.out.println("Unable to crawl! Please try again.");
		} finally {
			driver.quit();
		}
	}

	private static String constructURL(String website) {
		switch (website) {
			case "point2homes":
				return String.format("https://www.point2homes.com/CA/Real-Estate-Listings/ON.html");
			case "remax":
				return String.format("https://www.remax.ca/");
			case "realtor":
				return String.format("");
			case "propertyguys":
				return String.format("https://propertyguys.com/search/ca/");
			default:
				System.out.println("Unsupported website: " + website);
		}
		return "";
	}

	public static String GetBedsValue(WebElement item) {
		WebElement bedElement = item.findElement(By.cssSelector("li.ic-beds"));
		String bedsValue = bedElement.getText();
		return bedsValue;
	}

	public static String GetBathsValue(WebElement item) {
		// Find the <li> element with the specified class name
		WebElement bathElement = item.findElement(By.cssSelector("li.ic-baths"));

		// Extract the text content of the <li> element
		String bathsValue = bathElement.getText().trim();
		return bathsValue;
	}

	public static String GetPropertyTypeValue(WebElement item) {

		// Find the <li> element with the specified class name
		WebElement porpertyTypeElement = item.findElement(By.cssSelector("li.property-type.ic-proptype"));

		// Extract the text content of the <li> element
		String propertyType = porpertyTypeElement.getText().trim();
		return propertyType;
	}

	public static String GetPriceValue(WebElement item) {
		// Find the <div> element with the specified class name
		WebElement priceElement = item.findElement(By.cssSelector("div.price"));

		// Extract the value of the "data-price" attribute
		String price = priceElement.getAttribute("data-price");

		return price;
	}

	public static String GetImageVlue(WebElement item) {
		// Find the img element
		WebElement imgElement = item.findElement(By.tagName("img"));

		// Extract the value of the src attribute
		return imgElement.getAttribute("src");
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

		return parts;
	}

	// ************************************ Point2Homes.com
	// ************************************
	public static void Crawlp2h(String defaultUrl, String location) {
		try {
			// System.out.println("*********************************");
			driver.get(defaultUrl);
			Thread.sleep(2000); // Adding a wait for page load
			// Wait for the listings to load
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("listings")));
			List<WebElement> items = driver.findElements(By.cssSelector(".items > li"));

			for (WebElement item : items) {
				JSONObject itemObject = new JSONObject();
				try {
					String dataAddress = item.findElement(By.className("address-container"))
							.getAttribute("data-address");

					// Split the full address into street address, city, and province
					String[] addressParts = dataAddress.split(",\\s*", 3); // Split on comma and optional whitespace
					String streetAddress = addressParts[0];
					String city = addressParts[1];
					// System.out.println(city);

					String bedValue = GetBedsValue(item);
					String bathsValue = GetBathsValue(item);
					String propertyType = GetPropertyTypeValue(item);
					String priceValue = GetPriceValue(item);

					// Find the <div> element with the specified class name
					WebElement openHouseElement = item.findElement(By.cssSelector("div.open-house-right"));
					// Find the nested <div> element with the specified class name containing open
					// house information
					WebElement openHouseInfoElement = openHouseElement
							.findElement(By.cssSelector("div.open-house-line"));
					String[] DayDateTime = GetDayDateTime(openHouseInfoElement);

					itemObject.put("location", dataAddress.toLowerCase());
					itemObject.put("city", city.toLowerCase());
					itemObject.put("beds", bedValue);
					itemObject.put("baths", bathsValue);
					itemObject.put("propertyType", propertyType.toLowerCase());
					itemObject.put("price", priceValue);
					jsonArray.add(itemObject);

				} catch (org.openqa.selenium.NoSuchElementException e) {
					// System.out.println("Unable to find required elements. Please try again.");
				}
			}
		} catch (Exception e) {
			// System.out.println("Unexpected error occured. " + e.getMessage());
		} finally {
			// driver.quit();
		}
	}

	// ************************************ ReMax.ca
	// ************************************
	public static void CrawlRemax(String defaultUrl, String location) {

		try {
			// System.out.println("*********************************");
			for (int i = 0; i < 4; i++) {
				String Url = String.format("%s/%s?pageNumber=%s", defaultUrl, location, i + 1);
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

					WebElement propertyAddressElement = listingCard
							.findElement(By.cssSelector("[data-cy='property-address']"));

					// Get the text content of the element
					String propertyAddress = propertyAddressElement.getText();
					// Split the text content to separate address and city/province
					String[] addressParts = propertyAddress.split(", ");
					String address = addressParts[0]; // Extracting the address part
					String cityProvince = addressParts[1]; // Extracting the city and province part

					// Split cityProvince to separate city and province
					String[] cityProvinceParts = cityProvince.split(" ");
					String city = cityProvinceParts[0]; // Extracting the city
					if (cityProvinceParts.length > 1) {
						String province = cityProvinceParts[1]; // Extracting the province
					}

					itemObject.put("location", propertyAddress.toLowerCase());
					itemObject.put("city", city.toLowerCase());
					itemObject.put("beds", bed);
					itemObject.put("baths", bath);
					itemObject.put("propertyType", "residential");
					itemObject.put("price", price);
					jsonArray.add(itemObject);
				}
			}
		} catch (Exception e) {
			// System.out.println("Unexpected error occured. " + e.getMessage());
		} finally {
			// driver.quit();
		}
	}

	// ************************************ propertyguys.com
	// ************************************
	public static void CrawlPropertyGuys(String defaultUrl, String location) {
		String Url = String.format("%s%s", defaultUrl, location);
		driver.get(Url);
		WebElement listingListDiv = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.className("listing-list")));

		// Find all divs with class "listing-container" within listingListDiv
		List<WebElement> listingContainers = listingListDiv.findElements(By.className("listing-container"));
		// Loop through each listing-container
		for (WebElement container : listingContainers) {
			// Extract data
			String bedroom = container.findElement(By.className("bedroom")).getText().trim();
			String bathroom = container.findElement(By.className("bathroom")).getText().trim();
			String street = container.findElement(By.className("street")).getText().trim();

			String cityProvince = container.findElement(By.className("city-province")).getText().trim();
			String[] cityProvinceArray = cityProvince.split(",\\s*", 2); // Split on comma and optional whitespace
			String city = cityProvinceArray[0];

			String price = container.findElement(By.className("price")).getText().trim();

			// Create a JSONObject for current listing
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("location", String.format("%s%s", street, cityProvince).toLowerCase());
			jsonObject.put("city", city.toLowerCase());
			jsonObject.put("beds", bedroom);
			jsonObject.put("baths", bathroom);
			jsonObject.put("propertyType", "residential");
			jsonObject.put("price", price);

			// Add the JSONObject to the JSONArray
			jsonArray.add(jsonObject);
		}
	}
}