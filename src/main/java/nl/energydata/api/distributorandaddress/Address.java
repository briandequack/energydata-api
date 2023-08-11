package nl.energydata.api.distributorandaddress;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Address implements IResponse{
	
	private String countryCode;
	
	private String country;
	
	private String region;
	
	private String city;
	
	private String zipCode;

	private String street;
	
	private String houseNumber;
	
	private String houseNumberSuffix;

}
