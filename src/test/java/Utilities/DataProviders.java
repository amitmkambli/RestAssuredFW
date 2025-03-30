package Utilities;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.testng.annotations.DataProvider;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class DataProviders {
	
	//we can return a 2 dimensions objects array or an iterator
	//LinkedHashMap : to get ordered map
	@DataProvider(name = "dataFromExcel")
	public static Iterator<LinkedHashMap<String, String>> excelReader(Method methodName) throws EncryptedDocumentException, IOException {
		
		List<LinkedHashMap<String, String>> dataFromExcel = new ArrayList<>();
		
		Workbook workbook =   WorkbookFactory.create(new File(System.getProperty("user.dir")+ "/src/test/resources/testdata/TestData.xlsx" ));
		String sheetName = methodName.getName();
		Sheet sheet = workbook.getSheet(sheetName);
		
		int totalRows = sheet.getPhysicalNumberOfRows();
		LinkedHashMap<String,String> mapData;
		List<String> allKeys = new ArrayList<>();
		DataFormatter dataFormatter = new DataFormatter();
		
		for(int i = 0; i< totalRows ; i++) {
			 mapData = new LinkedHashMap<>();
			 if( i == 0) {
	                int totalCols = sheet.getRow(0).getPhysicalNumberOfCells();
	                for (int j = 0; j < totalCols; j++) {
	                    allKeys.add(sheet.getRow(0).getCell(j).getStringCellValue());
	                }
	            }
			 else {
	                int totalCols = sheet.getRow(i).getPhysicalNumberOfCells();
	                for (int j = 0; j < totalCols; j++) {
	                    String cellValue = dataFormatter.formatCellValue(sheet.getRow(i).getCell(j));

	                    mapData.put(allKeys.get(j), cellValue);
	                }
	                dataFromExcel.add(mapData);
	            }
		}
		
		dataFromExcel = dataFromExcel.stream()
		    		  .filter(map -> map.get("Enabled").equalsIgnoreCase("Y"))
		    		  .collect(Collectors.toList());
      
		return dataFromExcel.iterator();
		
	}
}