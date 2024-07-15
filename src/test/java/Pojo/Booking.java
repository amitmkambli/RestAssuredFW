package Pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
	private String firstname = "James";
	private String lastname = "Brown";
	private int totalprice = 111;
	private boolean depositpaid = true;
	private BookingDates bookingdates = new BookingDates("2018-01-01", "2019-01-01");
	private String additionalneeds = "Breakfast";
}
