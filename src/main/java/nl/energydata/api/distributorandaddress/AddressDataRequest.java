package nl.energydata.api.distributorandaddress;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDataRequest {
	String zipCode;
	String houseNumber;
	String houseNumberSuffix;
}
