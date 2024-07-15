package Utilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
	public static String getProperty(String propertyName) {
		Properties properties = new Properties();
		
		//try(InputStream inputStream = ConfigReader.class.getClassLoader().getResourceAsStream("properties/qaconfig.properties")){
		try(InputStream inputStream = ResourceLoader.getResource("properties/qaconfig.properties")){
			if (inputStream == null) {
				System.out.println("Sorry, unable to find config.properties");
				return propertyName;
			} 
			properties.load(inputStream);
		} catch(IOException ex) {
			ex.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return properties.getProperty(propertyName);
	}
}
