package Tests;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import Pojo.Booking;
import Pojo.BookingDates;
import Utilities.DataGenerator;
import Utilities.ResourceLoader;

public class CreatePayloads {
		
	public static String CreateToken() {
		return "{\r\n"
				+ "    \"username\" : \"admin\",\r\n"
				+ "    \"password\" : \"password123\"\r\n"
				+ "}";
	}
	
	public static String payloadFromFile(String path) {
		try {
				//return new String(Files.readAllBytes(Paths.get(path)));
				return new String(ResourceLoader.getResource(path).readAllBytes(), StandardCharsets.UTF_8);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		return "Error occured";
	}
	
	public static Booking createPaylodViaObjectMapper() {
		String filePath = System.getProperty("user.dir") + "/src/test/resources/jsonfiles/book.json";
		ObjectMapper obj = new ObjectMapper();
		//@NoArgsConstructor, @AllArgsConstructor added after error in below line on @Binding for builder method
		Booking booking = null;
		try {
			booking = obj.readValue(new File(filePath), Booking.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//updating json fields
		booking.setFirstname(DataGenerator.getFirstName());
		booking.setLastname(DataGenerator.getLastName());
		int price = DataGenerator.getPrice();
		booking.setTotalprice(price);
		
		return booking;
	}
	
	public static Booking createPaylodViaLombok() {
		int price = DataGenerator.getPrice();
		
		BookingDates bd = BookingDates.builder().checkin("2019-01-01").checkout("2019-02-02").build();
		Booking booking = Booking.builder().firstname(DataGenerator.getFirstName()).lastname(DataGenerator.getLastName())
				.totalprice(price).depositpaid(true).bookingdates(bd).additionalneeds("COFFEE").build();
		return booking;
	}
	
}
