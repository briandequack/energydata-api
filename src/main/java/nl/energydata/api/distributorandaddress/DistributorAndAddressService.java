package nl.energydata.api.distributorandaddress;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DistributorAndAddressService {
	
	@Autowired
	Executor executor;
	
	@Autowired
	AddressService addressService;
	
	@Autowired
	DistributorService distributorService;

	public DistributorAndAddress get(AddressDataRequest addressDataRequest) throws Exception {	
		
		DistributorAndAddress distributorAndAddress = new DistributorAndAddress();

		/*
		Future<IResponse> futureAddress = null;
		IResponse address = null;
		try {
			futureAddress = executor.submitTask(() -> addressService.get(addressDataRequest));	
			address = futureAddress.get();
			distributorAndAddress.setAddress(address);
		} catch(Exception e) {
			throw new Exception(e);
		}
		*/
		Future<IResponse> futureDistributor = null;
		IResponse distributor = null;
		try {
			futureDistributor = executor.submitTask(() -> distributorService.get(addressDataRequest.getZipCode()));	
			distributor = futureDistributor.get();
			distributorAndAddress.setDistributor(distributor);
		} catch(Exception e) {
			throw new Exception(e);
		}
			
		return distributorAndAddress;
	
	}


}
