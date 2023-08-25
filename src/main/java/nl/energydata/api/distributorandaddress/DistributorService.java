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
			String distributorEan = null;
		
		    JSONObject gridOperator = (JSONObject) gridOperators.get(0);
	
		    distributorName = (String) gridOperator.get("gridOperatorName");
		    distributorEan = (String) gridOperator.get("gridOperatorEan");

		    Distributor distributor = new Distributor();
		    distributor.setDistributorName(distributorName);
		    distributor.setDistributorEan(distributorEan);
		    return distributor;
			} catch(Exception e) {
				throw new Exception("Distributor not found.");
			}
		  
		} catch (Exception e) {
			throw new Exception(e);
			
		}
			
	}
	
	public BigDecimal getElecRate(Optional<String> gridOperatorEAN) {
		BigDecimal rate3x25A = BigDecimal.ZERO;
		//Coteq Netbeheer B.V. 
		if (gridOperatorEAN.isPresent() && "8716916000004".equals(gridOperatorEAN.get())) {
			rate3x25A = new BigDecimal(0.69670);
	    }
		//Enexis B.V.  
		else if (gridOperatorEAN.isPresent() && "8716948000003".equals(gridOperatorEAN.get())) {
			rate3x25A = new BigDecimal(0.73793);
	    }
		//Liander N.V. 
		else if (gridOperatorEAN.isPresent() && "8716871000002".equals(gridOperatorEAN.get())) {
			rate3x25A = new BigDecimal(0.78403);
	    }
		//N.V. RENDO  
		else if (gridOperatorEAN.isPresent() && "8716912000008".equals(gridOperatorEAN.get())) {
			rate3x25A = new BigDecimal(0.60802);
	    }
		//Stedin Netbeheer B.V.
		else if (gridOperatorEAN.isPresent() && "8716892000005".equals(gridOperatorEAN.get())) {
			rate3x25A = new BigDecimal(0.80279);
	    }
		//Westland Infra Netbeheer B.V. 
		else if (gridOperatorEAN.isPresent() && "8716878999996".equals(gridOperatorEAN.get())) {
			rate3x25A = new BigDecimal(0.84748);
	    } else {
	    	rate3x25A = new BigDecimal(0.78403);
	    }
		return rate3x25A;
	}
	
	public BigDecimal getGasRate(Optional<String> gridOperatorEAN) {
		BigDecimal rateG4G6 = BigDecimal.ZERO;
		//Coteq Netbeheer B.V. 
		if (gridOperatorEAN.isPresent() && "8716916000004".equals(gridOperatorEAN.get())) {
			rateG4G6 = new BigDecimal(0.4908);
	    }
		//Enexis B.V.  
		else if (gridOperatorEAN.isPresent() && "8716948000003".equals(gridOperatorEAN.get())) {
			rateG4G6 = new BigDecimal(0.460909);
	    }
		//Liander N.V. 
		else if (gridOperatorEAN.isPresent() && "8716871000002".equals(gridOperatorEAN.get())) {
			rateG4G6 = new BigDecimal(0.54380);
	    }
		//N.V. RENDO  
		else if (gridOperatorEAN.isPresent() && "8716912000008".equals(gridOperatorEAN.get())) {
			rateG4G6 = new BigDecimal(0.64109);
	    }
		//Stedin Netbeheer B.V.
		else if (gridOperatorEAN.isPresent() && "8716892000005".equals(gridOperatorEAN.get())) {
			rateG4G6 = new BigDecimal(0.6877);
	    }
		//Westland Infra Netbeheer B.V. 
		else if (gridOperatorEAN.isPresent() && "8716878999996".equals(gridOperatorEAN.get())) {
			rateG4G6 = new BigDecimal(0.48410);
	    } else {
	    	rateG4G6 = new BigDecimal(0.78403);
	    }
		return rateG4G6;
	}
}
