package Reports;

import java.util.List;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.markuputils.CodeLanguage;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import io.restassured.http.Header;



public class ExtentReportManager {
	
	public static ExtentReports extentReports;
	
	public static ExtentReports createInstance(String fileName, String reportName, String documentTitle) {
		ExtentSparkReporter sparkReport = new ExtentSparkReporter(fileName);
		sparkReport.config().setReportName(reportName);
		sparkReport.config().setDocumentTitle(documentTitle);
		sparkReport.config().setEncoding("utf-8");
		
		extentReports = new ExtentReports();
		extentReports.attachReporter(sparkReport);
		
		return extentReports;
	}
	
	public static void logPassDetails(String log) {
        Setup.extentTest.get().pass(MarkupHelper.createLabel(log, ExtentColor.GREEN));
    }
	
    public static void logFailureDetails(String log) {
        Setup.extentTest.get().fail(MarkupHelper.createLabel(log, ExtentColor.RED));
    }
    
    public static void logExceptionDetails(String log) {
        Setup.extentTest.get().fail(log);
    }
    
    public static void logInfoDetails(String log) {
        Setup.extentTest.get().info(MarkupHelper.createLabel(log, ExtentColor.GREY));
    }
    
    public static void logJson(String json) {
        Setup.extentTest.get().info(MarkupHelper.createCodeBlock(json, CodeLanguage.JSON));
    }
    public static void logHeaders(List<Header> headersList) {
    	//used stream of map below 
        String[][] arrayHeaders = headersList.stream().map(header -> new String[] {header.getName(), header.getValue()})
                        .toArray(String[][] :: new);
        Setup.extentTest.get().info(MarkupHelper.createTable(arrayHeaders));
    }

}
