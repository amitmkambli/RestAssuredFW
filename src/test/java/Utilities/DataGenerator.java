package Utilities;

import java.util.Random;

import net.datafaker.Faker;

public class DataGenerator {
	private static Faker faker = new Faker();
	
	public static String getFirstName() {
		return faker.name().firstName();
	}

	public static String getLastName() {
		return faker.name().lastName();
	}

	public static int getPrice() {
		//return Integer.parseInt(faker.numerify("###"));
		return Integer.parseInt(faker.number().digits(3));
	}
	
}
