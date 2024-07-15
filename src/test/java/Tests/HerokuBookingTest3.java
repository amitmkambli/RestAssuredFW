package Tests;

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import Pojo.Booking;
import Pojo.BookingDates;
import Reports.ExtentReportManager;
import Utilities.ConfigReader;
import Utilities.DataGenerator;
import Utilities.ResourceLoader;

import java.nio.file.Files;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.datafaker.Faker;

import static io.restassured.RestAssured.*;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class HerokuBookingTest3 {	
	
	private static Map<String, Object> headers;
	
	@BeforeClass
	public void setup() {
		headers = new HashMap<String, Object>();
	}
		
	@Test(priority = 1)
	public void getToken(ITestContext context) throws Exception {
		
		Response response = SpecBuilders.getResponse(new HashMap<String, Object>(), "/auth", CreatePayloads.CreateToken(), "post");
		String token = response
				.then()
				.body("token", Matchers.notNullValue())
				.body("token", Matchers.matchesRegex("[a-z0-9]+"))
				.extract()
				.response()		
				.jsonPath().get("token").toString()
				;
		
		System.out.println("token -> "+token);
		context.setAttribute("token", token);
		headers.put("Cookie","token="+context.getAttribute("token"));
	}
	
	@Test(priority = 2)
	public void createBookingViaLombok(ITestContext context) throws Exception {
		
		System.out.println("token from context -> "+context.getAttribute("token"));
		Response response = SpecBuilders.getResponse(headers, "/booking", CreatePayloads.createPaylodViaObjectMapper(), "post");
		String bookingid = response.jsonPath().get("bookingid").toString();
		System.out.println("bookingid -> "+bookingid);
		
		String filePathWrite = System.getProperty("user.dir") + "/src/test/resources/responses/book_post.json";
		Files.write(Paths.get(filePathWrite), response.asByteArray());
		
		context.setAttribute("bookingid3", bookingid);
	}
	
	@Test(priority = 3, dataProviderClass= Utilities.DataProviders.class, dataProvider = "dataFromExcel" ,enabled = true)
	public void patchUpdateBooking(ITestContext context, LinkedHashMap<String, String> map) throws Exception {
		String bookingID = context.getAttribute("bookingid3").toString();
		String basePath = "/booking/".concat(bookingID);
		
		String patch = "{\r\n"
				+ "    \"firstname\" : \""+ map.get("firstName") +"\",\r\n"
				+ "    \"lastname\" : \""+ map.get("lastName") +"\",\r\n"
				+ "    \"totalprice\" : "+ map.get("price") +"\r\n"
				+ "}";
					
		Response responsePatch = SpecBuilders.getResponse(headers, basePath, patch, "patch");
		
		responsePatch
		.then()
		.body(	"firstname", Matchers.equalTo(map.get("firstName")),
				"lastname", Matchers.equalTo(map.get("lastName")),
				"totalprice", Matchers.equalTo(Integer.parseInt(map.get("price")))
			);
		
		ObjectMapper objMap = new ObjectMapper();
		String resPatch = objMap.writerWithDefaultPrettyPrinter().writeValueAsString(responsePatch.asString());
		
		Response responseGet = SpecBuilders.getResponse(basePath, "get");
		String resGet = objMap.writerWithDefaultPrettyPrinter().writeValueAsString(responseGet.asString());
		
		Assert.assertTrue(resPatch.equals(resGet));
		
	}

}
