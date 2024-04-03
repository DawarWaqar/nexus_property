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

	public static void main() {
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
			String[] websites = {"point2homes", "remax", "propertyguys"};
			for (String website : websites) {
				String url = constructURL(website);
				Thread.sleep(2000); // Adding a wait for page load
				switch (website) {
				case "point2homes":
					Crawlp2h(url,"on/windsor");
					break;
				case "remax":
					CrawlRemax(url, "on/windsor");
					break;
				case "propertyguys":
					CrawlPropertyGuys(url, "on/windsor");
					break;
				}
			}

			// Write JSON array to file
			try (FileWriter file = new FileWriter("data.json")) {
				file.write(jsonArray.toJSONString());
				System.out.println("JSON array written to output.json file");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			driver.quit();
		}
	}

	private static String constructURL(String website) 
	{
		switch (website) {
		case "point2homes":
			return String.format("https://www.point2homes.com/CA/Real-Estate-Listings/ON/Windsor.html");
		case "remax":
			return String.format("https://www.remax.ca/");
		case "realtor":
			return String.format("");
		case "propertyguys":
			return String.format("https://propertyguys.com/search/ca/");
		default:
			throw new IllegalArgumentException("Unsupported website: " + website);
		}
	}

	public static String GetBedsValue(WebElement item) 
	{
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

	public static String[] GetDayDateTime(WebElement item)
	{
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

	//************************************ Point2Homes.com ************************************
	public static void Crawlp2h(String defaultUrl, String location) {
		try {

			driver.get(defaultUrl);
			Thread.sleep(2000); // Adding a wait for page load
			// Wait for the listings to load
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("listings")));
			List<WebElement> items = driver.findElements(By.cssSelector(".items > li"));

			for (WebElement item : items) {
				JSONObject itemObject = new JSONObject();
				try {

					//					String dataAddressValue = GetaddressValue(item);
					// Find the div containing the property address
					WebElement addressContainerDiv = driver.findElement(By.className("address-container"));

					// Extract the full address text
					String fullAddress = addressContainerDiv.getText().trim();

					// Split the full address into street address, city, and province
					String[] addressParts = fullAddress.split(",\\s*", 3); // Split on comma and optional whitespace
					String streetAddress = addressParts[0];
					String city = addressParts[1];
					String province = addressParts[2];

					String bedValue = GetBedsValue(item);
					String bathsValue = GetBathsValue(item);
					String propertyType = GetPropertyTypeValue(item);
					String priceValue = GetPriceValue(item);
					String imageSrc = GetImageVlue(item);

					// Find the <div> element with the specified class name
					WebElement openHouseElement = item.findElement(By.cssSelector("div.open-house-right"));
					// Find the nested <div> element with the specified class name containing open house information
					WebElement openHouseInfoElement = openHouseElement.findElement(By.cssSelector("div.open-house-line"));
					String [] DayDateTime = GetDayDateTime(openHouseInfoElement);

					// Output the extracted components
					itemObject.put("location",  String.format("%s,%s,%s", streetAddress, city,province));
					itemObject.put("city", city);
					itemObject.put("province", province);
					itemObject.put("beds", bedValue);
					itemObject.put("baths", bathsValue);
					itemObject.put("propertyType", propertyType);
					itemObject.put("price", priceValue);
					itemObject.put("imageSrc", imageSrc);
					itemObject.put("image", imageSrc);
					jsonArray.add(itemObject);

				} catch (org.openqa.selenium.NoSuchElementException e) {
				}
			}
		} 
		catch(Exception e) {

		}
		finally {
			//			driver.quit();
		}
	}

	//************************************ ReMax.ca ************************************
	public static void CrawlRemax(String defaultUrl, String location) {

		try {
			for(int i=0; i<4 ; i++) {
				String Url = String.format("%s/%s-real-estate?pageNumber=%s",defaultUrl,location,i+1);
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

					// Find the div containing the property address
					WebElement propertyAddressDiv = driver.findElement(By.cssSelector("[data-cy='property-address']"));

					// Extract the street address
					String streetAddress = propertyAddressDiv.findElement(By.tagName("span")).getText().trim();

					// Extract the city and province
					String cityProvince = propertyAddressDiv.findElement(By.xpath("//span[2]")).getText().trim();

					// Split the cityProvince into city and province
					String[] cityProvinceParts = cityProvince.split(",\\s*", 2); // Split on comma and optional whitespace
					String city = null;
					String province = null;
					if(cityProvinceParts!=null && cityProvinceParts.length ==2){
					city = cityProvinceParts[0];
					province = cityProvinceParts[1];
					}

					// Find the img element
					WebElement imgElement = listingCard.findElement(By.tagName("img"));
					// Extract the value of the src attribute
					String srcValue = imgElement.getAttribute("src");

					itemObject.put("location",  String.format("%s,%s", streetAddress, cityProvince));
					itemObject.put("city", city);
					itemObject.put("province", province);
					itemObject.put("beds", bed);
					itemObject.put("baths", bath);
					itemObject.put("propertyType", "residential");
					itemObject.put("price", price);
					itemObject.put("image", srcValue);
					jsonArray.add(itemObject);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//			driver.quit();
		}
	}

	//************************************ propertyguys.com ************************************
	public static void CrawlPropertyGuys(String defaultUrl, String location) {
		String Url = String.format("%s%s",defaultUrl,location);
		driver.get(Url);
		// Find the div with class "listing-list"
		//        WebElement listingListDiv = driver.findElement(By.className("listing-list"));
		WebElement listingListDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("listing-list")));

		// Find all divs with class "listing-container" within listingListDiv
		List<WebElement> listingContainers = listingListDiv.findElements(By.className("listing-container"));
		System.out.println(listingContainers.size());

		// Loop through each listing-container
		for (WebElement container : listingContainers) {
			// Extract data
			String bedroom = container.findElement(By.className("bedroom")).getText().trim();
			String bathroom = container.findElement(By.className("bathroom")).getText().trim();
			String street = container.findElement(By.className("street")).getText().trim();

			String cityProvince = container.findElement(By.className("city-province")).getText().trim();
			String[] cityProvinceArray = cityProvince.split(",\\s*", 2); // Split on comma and optional whitespace
			String city = cityProvinceArray[0];
			String province = cityProvinceArray[1];

			String price = container.findElement(By.className("price")).getText().trim();
			String listingPhoto = container.findElement(By.className("listing-photo")).getAttribute("src");

			// Create a JSONObject for current listing
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("location", String.format("%s%s", street, cityProvince));
			jsonObject.put("city", city);
			jsonObject.put("province", province);
			jsonObject.put("beds", bedroom);
			jsonObject.put("baths", bathroom);
			jsonObject.put("propertyType", "residential");
			jsonObject.put("price", price);
			jsonObject.put("image", listingPhoto);

			// Add the JSONObject to the JSONArray
			jsonArray.add(jsonObject);
		}
	}
}