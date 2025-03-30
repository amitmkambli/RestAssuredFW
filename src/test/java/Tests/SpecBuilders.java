package Tests;

import static io.restassured.RestAssured.*;

import java.util.Map;

import Utilities.ConfigReader;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.QueryableRequestSpecification;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import io.restassured.specification.SpecificationQuerier;
import Reports.ExtentReportManager;

public class SpecBuilders {
	
	public static RequestSpecification reqSpec(Map headers, String basePath,  Object body) {
		return new RequestSpecBuilder()
					.log(LogDetail.ALL)
					.addHeaders(headers)
					.setBaseUri(ConfigReader.getProperty("baseurl"))
					.setBasePath(basePath)
					.setContentType(ContentType.JSON)
					//.addQueryParams(map for queries)
					//.addParams(map for params)
					.setBody(body)
					.build();
	}
	
	//getRequest
	public static RequestSpecification reqSpec(String basePath) {
		return new RequestSpecBuilder()
					.log(LogDetail.ALL)
					.setBaseUri(ConfigReader.getProperty("baseurl"))
					.setBasePath(basePath)
					//.addQueryParams(map for queries)
					//.addParams(map for params)
					.setContentType(ContentType.JSON)
					.build();
	}
	
	public static ResponseSpecification resSpec() {
		return new ResponseSpecBuilder()
				.log(LogDetail.ALL)
				.expectContentType(ContentType.JSON)
				.expectStatusCode(200)
				.build();
	}
	
	public static void printRequestLogs(RequestSpecification spec, String requestType) {
		QueryableRequestSpecification reqspec = SpecificationQuerier.query(spec);
		Reports.ExtentReportManager.logInfoDetails("Base URI is " + reqspec.getBaseUri());
		Reports.ExtentReportManager.logInfoDetails("Base path is " + reqspec.getBasePath());
        //methods : get , post , put , delete
        ExtentReportManager.logInfoDetails("Method is " + requestType);
        ExtentReportManager.logInfoDetails("Headers are ");
        ExtentReportManager.logHeaders(reqspec.getHeaders().asList());
        if ("postpatchupdate".contains(requestType)) {
			ExtentReportManager.logInfoDetails("Request body is ");
			ExtentReportManager.logJson(reqspec.getBody());
		}
	}
	
	public static void printResponseLogs(Response response) {
		ExtentReportManager.logInfoDetails("Response status is " + response.getStatusCode());
        ExtentReportManager.logInfoDetails("Response Headers are ");
        ExtentReportManager.logHeaders(response.getHeaders().asList());
        ExtentReportManager.logInfoDetails("Response body is ");
        ExtentReportManager.logJson(response.getBody().prettyPrint());
	}
	
	public static Response getResponse(Map headers, String basePath, Object body, String requestType) {
		RequestSpecification rSpec = reqSpec(headers, basePath, body);
		Response response = switch (requestType) {
			case "post" -> given().spec(rSpec).post();
			case "patch" -> given().spec(rSpec).patch();
			default ->throw new IllegalArgumentException("Unexpected value: " + requestType);
		};

		printRequestLogs(rSpec, requestType);
		printResponseLogs(response);
		return response;
	}
	
	public static Response getResponse(String basePath, String requestType) {
		RequestSpecification rSpec = reqSpec(basePath);
		Response response = given().spec(rSpec).get();
		printRequestLogs(rSpec, requestType);
		printResponseLogs(response);
		return response;
	}

}
