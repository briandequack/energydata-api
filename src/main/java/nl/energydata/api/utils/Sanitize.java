package nl.energydata.api.utils;

public class Sanitize {

	public static String removeStreetNumber(String streetNameWithNumber) {
		String streetNameWithoutNumber =  streetNameWithNumber.replaceAll("\\d+", "");
		return streetNameWithoutNumber.trim();
	}

}
