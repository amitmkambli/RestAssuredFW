package Reports;

import java.util.Arrays;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;


public class Setup implements ITestListener {
	
	public static ExtentReports extentReports;
	public static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<ExtentTest>();
	
	@Override
	public void onStart(ITestContext context) {
		String fileName = "ExtentReport.html";
		String path = System.getProperty("user.dir") + "/reports/" + fileName;
		extentReports = ExtentReportManager.createInstance(path, "Test API Automation Report", "Test ExecutionReport");
	}

	@Override
	public void onFinish(ITestContext context) {
		 if(extentReports != null) {
			 extentReports.flush();
		 }
	}
	
	@Override
	public void onTestStart(ITestResult result) {
		ExtentTest test = extentReports.createTest("Test Name " + result.getTestClass().getName() + " - " +	result.getMethod().getMethodName());
		extentTest.set(test);
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		ExtentReportManager.logPassDetails("PASS");
	}

	@Override
	public void onTestFailure(ITestResult result) {
		ExtentReportManager.logExceptionDetails(result.getThrowable().getMessage());
		String stackTrace = Arrays.toString(result.getThrowable().getStackTrace());
		stackTrace = stackTrace.replaceAll(",", "</br>");
		String formmatedTrace = "<details>\n" +
                "    <summary>Click Here To See Exception Logs</summary>\n" +
                "    " + stackTrace + "\n" +
                "</details>\n";
        ExtentReportManager.logExceptionDetails(formmatedTrace);
	}

	@Override
	public void onTestSkipped(ITestResult result) {
	}






	
	
}
