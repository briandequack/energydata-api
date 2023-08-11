package nl.energydata.api.distributorandaddress;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import nl.energydata.api.utils.Http;

@Service
public class DistributorService {
	
	@Value("${property.EDSN_KEY}")
	String EDSNKey;
	
	public Distributor get(String postalCode) throws Exception{
	
		String url = String.format("https://gateway.edsn.nl/eancodeboek/v1/gridoperator?postalCode=%s", postalCode);

		Map<String, String> headers = new HashMap<>();
		headers.put("accept", "application/json");
		headers.put("X-API-Key", EDSNKey);

		JSONObject json;
		try {
			json = Http.callApi(url, headers);
			try {
			JSONArray gridOperators = (JSONArray) json.get("gridOperators");    
			String distributorName = null;
		
		    JSONObject gridOperator = (JSONObject) gridOperators.get(0);
	
		    distributorName = (String) gridOperator.get("gridOperatorName");

		    Distributor distributor = new Distributor();
		    distributor.setDistributorName(distributorName);
		    return distributor;
			} catch(Exception e) {
				throw new Exception("Distributor not found.");
			}
		  
		} catch (Exception e) {
			throw new Exception(e);
			
		}
			
	}
	
	public BigDecimal getElecRate(Optional<String> name) {
		BigDecimal rate3x25A = new BigDecimal(0.78403);
		return rate3x25A;
	}
	
	public BigDecimal getGasRate(Optional<String> name) {
		BigDecimal rateG4G6 = new BigDecimal(0.54380);
		return rateG4G6;
	}
}
