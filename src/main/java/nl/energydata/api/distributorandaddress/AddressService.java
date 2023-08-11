package nl.energydata.api.distributorandaddress;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import nl.energydata.api.utils.Http;
import nl.energydata.api.utils.Sanitize;

@Service
public class AddressService {
	
	@Value("${property.GEOCODE_KEY}")
	private String geoCodeKey;
	
	public Address get(AddressDataRequest addressDataRequest){
		
		String url = String.format("https://geocode.xyz/%s?json=1&auth=%s", addressDataRequest.getZipCode(), geoCodeKey);
		JSONObject json;
		
		try {
			json = Http.callApi(url);
		} catch (IOException | InterruptedException | ParseException e) {
			e.printStackTrace();
			return null;
		} 

		JSONObject standard = (JSONObject) json.get("standard");     
		Address address = new Address();
		address.setCountryCode((String) standard.get("prov"));
		address.setCountry((String) standard.get("countryname"));
		address.setRegion((String) standard.get("region"));
		address.setCity((String) standard.get("city"));
		address.setZipCode(addressDataRequest.getZipCode());
		address.setStreet(Sanitize.removeStreetNumber((String) standard.get("addresst")));
		address.setHouseNumber(addressDataRequest.getHouseNumber());
		address.setHouseNumberSuffix(addressDataRequest.getHouseNumberSuffix());
		
		return address;	
	}


}
