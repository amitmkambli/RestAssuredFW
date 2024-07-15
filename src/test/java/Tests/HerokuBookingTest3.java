package Tests;

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import Pojo.Booking;
import Pojo.BookingDates;
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
import java.util.HashMap;
import java.util.Map;

public class HerokuBookingTest3 {	
	
	private static Map<String, Object> headers;
	private static Map<String, Object> responseFields;
	
	@BeforeClass
	public void setup() {
		headers = new HashMap<String, Object>();
		responseFields = new HashMap<String, Object>();
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
	
	@Test(priority = 4)
	public void createBookingViaLombok(ITestContext context) throws Exception {
		
		System.out.println("token from context -> "+context.getAttribute("token"));
		Response response = SpecBuilders.getResponse(headers, "/booking", CreatePayloads.createPaylodViaObjectMapper(), "post");
		String bookingid = response.jsonPath().get("bookingid").toString();
		System.out.println("bookingid -> "+bookingid);
		
		String filePathWrite = System.getProperty("user.dir") + "/src/test/resources/responses/book_post.json";
		Files.write(Paths.get(filePathWrite), response.asByteArray());
		
		context.setAttribute("bookingid3", bookingid);
	}
	
	@Test(priority = 5, enabled = true)
	public void patchUpdateBooking(ITestContext context) throws Exception {
		String bookingID = context.getAttribute("bookingid3").toString();
		String basePath = "/booking/".concat(bookingID);
		
		String patch = "{\r\n"
				+ "    \"firstname\" : \"James\",\r\n"
				+ "    \"lastname\" : \"Brown\",\r\n"
				+ "    \"totalprice\" : 111\r\n"
				+ "}";
					
		Response response = given().log().all(true).headers(headers)
				.baseUri(ConfigReader.getProperty("baseurl"))
				.basePath(basePath)
				.body(patch)
				.contentType(ContentType.JSON)
				.when()
					.patch()
				.then().log().all(true)
					.statusCode(200).extract().response();
		
		response
		.then()
		.body(	"firstname", Matchers.equalTo("James"),
				"lastname", Matchers.equalTo("Brown"),
				"totalprice", Matchers.equalTo(111)
			);
		responseFields.put("firstname", response.jsonPath().getString("firstname"));
		responseFields.put("lastname", response.jsonPath().getString("lastname"));
		responseFields.put("totalprice", response.jsonPath().getInt("totalprice"));
	}
	
	@Test(priority = 6, enabled = true)
	public void checkGetResponse(ITestContext context) throws Exception {
		
		String bookingID = context.getAttribute("bookingid3").toString();
		String basePath = "/booking/".concat(bookingID);
		Response response = SpecBuilders.getResponse(basePath, "get");
		response
		.then().spec(SpecBuilders.resSpec())
		.extract().response();
		
		response
		.then()
		.body(	"firstname", Matchers.equalTo(responseFields.get("firstname")),
				"lastname", Matchers.equalTo(responseFields.get("lastname")),
				"totalprice", Matchers.equalTo(responseFields.get("totalprice"))
			);
	}

}
