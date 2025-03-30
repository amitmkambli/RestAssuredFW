package goRestTest;

import java.util.*;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.QueryableRequestSpecification;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import io.restassured.specification.SpecificationQuerier;
import net.datafaker.Faker;

enum endpoint{
	PostUser("/users"),
	GetUser("/users/");
	
	public String value;
	
	endpoint(String value) {
		this.value = value;
	}
};

public class APITest {
	
	// if API not working check if access_token is expired
	public static String access_token = "97cb691f4c211d6ab204c7352540b947eaad0a7b0acd4c4f6187578ffdacd668";

	@BeforeClass
	public void setup() {
		RestAssured.baseURI = "https://gorest.co.in/public/v2";
	}
	
	public static RequestSpecification baseRequest(Map<String, String> headers, Map<String, String> params, String basePath) {
		//given().log().all(true).headers(new HashMap<String, String>()).params(new HashMap<String, String>()).request(Method.GET);
		return new RequestSpecBuilder()
		.addFilter(new RequestLoggingFilter())
		.addFilter(new ResponseLoggingFilter())
		.addHeaders(headers)
		.addParams(params)
		.setBasePath(basePath)
		.build();
	}
	
	public static RequestSpecification requestSpec(Map<String, String> headers, Map<String, String> params, String basePath, Object requestBody) {
		//io.restassured.http.Method
		RequestSpecification request = RestAssured.given();
		request.headers(headers);
		request.params(params);
		request.basePath(basePath);
		if(requestBody != null) {
			request.body(requestBody);
		}
		request.filter( new RequestLoggingFilter());
		request.filter( new ResponseLoggingFilter());
		//below is invalid usage, will give error
		//request.request(method);
		return request;
	}
	
	@Test
	public void ceateUsers2() throws Exception {
		Response response = given()
				.spec(requestSpec(headers(), new HashMap<String, String>(), endpoint.PostUser.value, body()))
				.request(Method.POST)
				.then()
				.extract()
				.response();
		System.out.println(response.prettyPrint());//7800567
		
		QueryableRequestSpecification reqSpec = SpecificationQuerier.query(requestSpec(headers(), new HashMap<String, String>(), endpoint.GetUser.value, new String()));
		System.out.println(reqSpec.getBaseUri());
		System.out.println(reqSpec.getBasePath());
		System.out.println(reqSpec.getRequestParams());
		System.out.println(reqSpec.getHeaders());
		System.out.println(reqSpec.getBody().toString());
	}
	
	@Test
	public void getUsers1() throws Exception {
		
		Integer id = 7800567;
		Response response =  given()
							.spec(requestSpec(headers(), new HashMap<String, String>(), endpoint.GetUser.value + id, new String()))
							.request(Method.GET)
							.then()
							.extract().response();
		Assert.assertTrue(response.statusCode() == 200);
		System.out.println(response.header("x-ratelimit-remaining"));
		
		//@JsonIgnoreProperties(ignoreUnknown = true) --> will ignore 'id' in response
		POJO_GoRestUser data = response.as(POJO_GoRestUser.class);
		System.out.println(data.email());
		
	}
	
	
	public static Map<String, String> body() {
		Map<String, String> body = new HashMap<String, String>();
		Faker fake = new Faker();
		
		body.put("name", fake.name().fullName());
		body.put("gender", "male");
		body.put("email", fake.internet().emailAddress());
		body.put("status", "active");
		return body;
	}
	
	public static Map<String, String> headers() {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Accept", "application/json");
		headers.put("Content-Type","application/json");
		headers.put("Authorization", "Bearer " + access_token);
		return headers;
	}
	
	
	@Test
	public void createUsers() throws Exception {
		
		Response response = given()
							//.log().all(true)
							.body(body())
							.header("Accept", "application/json")
							.header("Content-Type","application/json")
							.header("Authorization", "Bearer 97cb691f4c211d6ab204c7352540b947eaad0a7b0acd4c4f6187578ffdacd668")
							.post("/users")
							.then()
							.log().all()
							.extract().response();
		
		System.out.println(response.body().prettyPrint());
		
	}
	
	@Test
	public void createUsers1() throws Exception {
				
		Response response =  given()
							.spec(baseRequest(headers(), new HashMap<String, String>(), endpoint.PostUser.value))
							.body(body())
							.request(Method.POST)
							.then()
							.extract().response();
		Assert.assertTrue(response.statusCode() == 201);
		Assert.assertTrue(response.statusLine().equalsIgnoreCase("HTTP/1.1 201 Created"));
		System.out.println(response.header("x-ratelimit-remaining"));
		System.out.println(response.jsonPath().getString("id"));
	}
	

	
	@Test
	public void getUsers() throws Exception {
		
		Integer id = 7774425;
		Response response =  given()
							.spec(baseRequest(headers(), new HashMap<String, String>(), endpoint.GetUser.value + id))
							.request(Method.GET)
							.then()
							.extract().response();
		Assert.assertTrue(response.statusCode() == 200);
		System.out.println(response.header("x-ratelimit-remaining"));
		
		//@JsonIgnoreProperties(ignoreUnknown = true) --> will ignore 'id' in response
		POJO_GoRestUser data = response.as(POJO_GoRestUser.class);
		System.out.println(data.email());
	}

	@Test
	public void updateUsers() throws Exception {
		
		Integer id = 7774425;
		Faker fake = new Faker();
		POJO_GoRestUser body = new POJO_GoRestUser(
				fake.name().fullName(), 
				fake.internet().emailAddress(), 
				null, 
				"inactive");
		
		Response response =  given()
							.spec(baseRequest(headers(), new HashMap<String, String>(), endpoint.GetUser.value + id))
							.body(body)
							.request(Method.PUT)
							.then()
							.extract().response();
		Assert.assertTrue(response.statusCode() == 200);
		System.out.println(response.header("x-ratelimit-remaining"));
		
		//@JsonIgnoreProperties(ignoreUnknown = true) --> will ignore 'id' in response
		POJO_GoRestUser data = response.as(POJO_GoRestUser.class);
		System.out.println(data.email());
	}
}
