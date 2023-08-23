package nl.energydata.api.distributorandaddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@CrossOrigin(origins = {"http://localhost:3000", "https://enerwijs.nl", "https://www.enerwijs.nl"})
@RequestMapping("/api/v1/address")
public class DistributorAndAddressController {
	
	private final DistributorAndAddressService distributorAndAddressService;
	
	@Autowired
    public DistributorAndAddressController(DistributorAndAddressService distributorAndAddressService) {
        this.distributorAndAddressService = distributorAndAddressService;
    }
	
	@PostMapping()
	public ResponseEntity<?> getAddressData(@RequestBody AddressDataRequest addressDataRequest) {
		try {
			DistributorAndAddress data = distributorAndAddressService.get(addressDataRequest);
	        return ResponseEntity.ok(data); 
		} catch (Exception e) {
			  return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No results.");
	
		}

	}
}
