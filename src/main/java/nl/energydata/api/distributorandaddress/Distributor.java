package nl.energydata.api.distributorandaddress;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Distributor implements IResponse{

	private String distributorName;
	private String distributorEan;
	
}
