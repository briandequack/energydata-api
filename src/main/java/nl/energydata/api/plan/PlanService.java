package nl.energydata.api.plan;
import java.math.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nl.energydata.api.distributorandaddress.DistributorService;
import nl.energydata.library.datacontainer.DurationCategory;
import nl.energydata.library.datacontainer.EnergyUtilityDataContainer;
import nl.energydata.library.datacontainer.EnergyUtilityDataContainerService;
import nl.energydata.library.energytax.EnergyTaxService;


@Service
public class PlanService {

	@Autowired
	EnergyUtilityDataContainerService energyUtilityDataContainerService;
	
	@Autowired
	DistributorService distributorService;
	
	@Autowired
	EnergyTaxService energyTaxService;
	
		
    public Map<DurationCategory, List<Plan>> calculate(PlanRequest planRequest) {
    	Optional<Integer> ELECTRICTY_USAGE_ON_PEAK_IN_KWH = Optional.ofNullable(planRequest.getELECTRICTY_USAGE_ON_PEAK_IN_KWH());
    	Optional<Integer> ELECTRICTY_USAGE_OFF_PEAK_IN_KWH = Optional.ofNullable(planRequest.getELECTRICTY_USAGE_OFF_PEAK_IN_KWH());
    	Optional<Integer> ELECTRICTY_PRODUCTION_IN_KWH = Optional.ofNullable(planRequest.getELECTRICTY_PRODUCTION_IN_KWH());
    	Optional<Integer> GAS_USAGE_IN_M3 = Optional.ofNullable(planRequest.getGAS_USAGE_IN_M3());
    	Optional<String> DISTRIBUTOR = Optional.ofNullable(planRequest.getDISTRIBUTOR());
    	Integer PAGE_NUMBER = planRequest.getPAGE_NUMBER();
    	Integer PAGE_SIZE = planRequest.getPAGE_SIZE();
    	
    	
    	Map<DurationCategory, List<EnergyUtilityDataContainer>> result = energyUtilityDataContainerService.getByUtilityRates(ELECTRICTY_USAGE_ON_PEAK_IN_KWH,ELECTRICTY_USAGE_OFF_PEAK_IN_KWH,ELECTRICTY_PRODUCTION_IN_KWH,GAS_USAGE_IN_M3,PAGE_NUMBER,PAGE_SIZE);
    	 Map<DurationCategory, List<Plan>> plansByDuration = new HashMap<>();
    	
    	 for (DurationCategory durationCategory : result.keySet()) {
    	        List<EnergyUtilityDataContainer> containers = result.get(durationCategory);
    	        List<Plan> plans = new ArrayList<>();
    	  
    	        for (EnergyUtilityDataContainer container : containers) {
    	        	
    	        	Plan plan = new Plan();
    	            plan.setContractDuration(durationCategory); 
    	            plan.setEnergyDataProviderName(container.getDataProvider().getName()); 
    	            
    	            BigDecimal originalUsageOnPeak = ELECTRICTY_USAGE_ON_PEAK_IN_KWH.map(BigDecimal::valueOf).orElse(BigDecimal.ZERO);
    	            BigDecimal originalUsageOffPeak = ELECTRICTY_USAGE_OFF_PEAK_IN_KWH.map(BigDecimal::valueOf).orElse(BigDecimal.ZERO);
    	            
    	            BigDecimal originalUsageGas = GAS_USAGE_IN_M3.map(BigDecimal::valueOf).orElse(BigDecimal.ZERO);
    	            BigDecimal usageGas = originalUsageGas;
    	            
    	            BigDecimal usageOnPeak = originalUsageOnPeak;
    	            BigDecimal usageOffPeak = originalUsageOffPeak;
    	                       
    	            BigDecimal solarProduction = ELECTRICTY_PRODUCTION_IN_KWH.map(BigDecimal::valueOf).orElse(BigDecimal.ZERO);
    	            BigDecimal solarProductionRemaining = ELECTRICTY_PRODUCTION_IN_KWH.map(BigDecimal::valueOf).orElse(BigDecimal.ZERO);
    	            BigDecimal totalUsageInKwh =  usageOnPeak.add(usageOffPeak);
    	            BigDecimal usageCap = new BigDecimal(2900);
    	            BigDecimal kwhMaxPrice =  new BigDecimal(0.4);
    	            
    	            BigDecimal m3MaxPrice =  new BigDecimal(1.45);
    	            
    	            if (solarProduction.compareTo(BigDecimal.ZERO) > 0) {
    	            	
    	            	
    	            	
    	                // 71.4% goes to on-peak
    	                BigDecimal solarProductionOnPeak = solarProduction.multiply(BigDecimal.valueOf(0.714));
    	                if (solarProductionOnPeak.compareTo(usageOnPeak) > 0) {
    	                    solarProductionRemaining = solarProductionRemaining.subtract(usageOnPeak);
    	                    usageOnPeak = BigDecimal.ZERO;
    	                } else {
    	                    usageOnPeak = usageOnPeak.subtract(solarProductionOnPeak);
    	                    solarProductionRemaining = solarProductionRemaining.subtract(solarProductionOnPeak);
    	                }

    	                // 28.6% goes to off-peak
    	                BigDecimal solarProductionOffPeak = solarProduction.multiply(BigDecimal.valueOf(0.286));
    	                if (solarProductionOffPeak.compareTo(usageOffPeak) > 0) {
    	                    solarProductionRemaining = solarProductionRemaining.subtract(usageOffPeak);
    	                    usageOffPeak = BigDecimal.ZERO;
    	                } else {
    	                    usageOffPeak = usageOffPeak.subtract(solarProductionOffPeak);
    	                    solarProductionRemaining = solarProductionRemaining.subtract(solarProductionOffPeak);
    	                } 
    	                
    	                if (solarProductionRemaining.compareTo(BigDecimal.ZERO) > 0) {
    	                    if (usageOnPeak.compareTo(BigDecimal.ZERO) > 0) {
    	                        if (solarProductionRemaining.compareTo(usageOnPeak) > 0) {
    	                            solarProductionRemaining = solarProductionRemaining.subtract(usageOnPeak);
    	                            usageOnPeak = BigDecimal.ZERO;
    	                        } else {
    	                            usageOnPeak = usageOnPeak.subtract(solarProductionRemaining);
    	                            solarProductionRemaining = BigDecimal.ZERO;
    	                        }
    	                    } else if (usageOffPeak.compareTo(BigDecimal.ZERO) > 0) {
    	                        if (solarProductionRemaining.compareTo(usageOffPeak) > 0) {
    	                            solarProductionRemaining = solarProductionRemaining.subtract(usageOffPeak);
    	                            usageOffPeak = BigDecimal.ZERO;
    	                        } else {
    	                            usageOffPeak = usageOffPeak.subtract(solarProductionRemaining);
    	                            solarProductionRemaining = BigDecimal.ZERO;
    	                        }
    	                    }
    	                }
    	                
    	                plan.setSolarProductionRate(container.getProducedElectricityRatePerKwh());   
    		    		plan.setSolarProductionRateInclAll(energyTaxService.elecRateAddAllTax((container.getElectricityOnPeakRatePerKwh())));
    		    		BigDecimal solarProductionDailyProduction = energyTaxService.numYearlyToDaily(new BigDecimal(ELECTRICTY_PRODUCTION_IN_KWH.get()));
    	            	plan.setSolarProductionDailyProfitsInclAll(solarProductionDailyProduction.multiply(plan.getSolarProductionRateInclAll()));
    	            	plan.setSolarProductionMonthlyProfitsInclAll(energyTaxService.numDailyToMonthly(plan.getSolarProductionDailyProfitsInclAll()));
    	            }
    	                       
    	            if (originalUsageOnPeak.compareTo(BigDecimal.ZERO) > 0) {
    	            	
    	            	plan.setOnPeakElecUsage(usageOnPeak);
    	            	
    	            	plan.setOnPeakElecRate(container.getElectricityOnPeakRatePerKwh()); 
    	            	plan.setOnPeakElecRateInclAll(energyTaxService.elecRateAddAllTax((container.getElectricityOnPeakRatePerKwh())));  
    	          
    	            	BigDecimal onPeakCappedUnits = energyTaxService.calculateCappedUnits(usageOnPeak, totalUsageInKwh, usageCap);
    	            	BigDecimal onPeakNonCappedUnits = energyTaxService.calculateNonCappedUnits(usageOnPeak, totalUsageInKwh, usageCap);
    	         
    	            	
    	            	BigDecimal onPeakCappedCosts = BigDecimal.ZERO;
    	            	BigDecimal onPeakNonCappedCosts = BigDecimal.ZERO;
    	            	BigDecimal onPeakTotalCosts = BigDecimal.ZERO;
    	            	
    	            	if(plan.getOnPeakElecRateInclAll().compareTo(kwhMaxPrice) >= 0) {
    	            	    onPeakCappedCosts = onPeakCappedUnits.multiply(kwhMaxPrice);
    	            	} else {
    	            	    onPeakCappedCosts = onPeakCappedUnits.multiply(plan.getOnPeakElecRateInclAll());
    	            	}
    	            	onPeakNonCappedCosts = onPeakNonCappedUnits.multiply(plan.getOnPeakElecRateInclAll());
    	            	
    	            	
    	            	onPeakTotalCosts = onPeakCappedCosts.add(onPeakNonCappedCosts);
    	            	
    	                plan.setOnPeakElecYearlyCostsInclAll(onPeakTotalCosts);
    	                
    	                plan.addElecTotalYearlyCostsInclAll(onPeakTotalCosts);
    	                
    	                
    	                
    		    	}

    	            if (originalUsageOffPeak.compareTo(BigDecimal.ZERO) > 0) {
    	            	
    	            	plan.setOffPeakElecUsage(usageOffPeak);
    	            	
    	            	plan.setOffPeakElecRate(container.getElectricityOffPeakRatePerKwh());
    	            	plan.setOffPeakElecRateInclAll(energyTaxService.elecRateAddAllTax((container.getElectricityOffPeakRatePerKwh())));
    	            	
    	            	BigDecimal offPeakCappedUnits = energyTaxService.calculateCappedUnits(usageOffPeak, totalUsageInKwh, usageCap);
    	            	BigDecimal offPeakNonCappedUnits = energyTaxService.calculateNonCappedUnits(usageOffPeak, totalUsageInKwh, usageCap);
    	         

    	            	BigDecimal offPeakCappedCosts = BigDecimal.ZERO;
    	            	BigDecimal offPeakNonCappedCosts = BigDecimal.ZERO;
    	            	BigDecimal offPeakTotalCosts = BigDecimal.ZERO;
    	            	
    	            	if(plan.getOffPeakElecRateInclAll().compareTo(kwhMaxPrice) >= 0) {
    	            	    offPeakCappedCosts = offPeakCappedUnits.multiply(kwhMaxPrice);
    	            	} else {
    	            	    offPeakCappedCosts = offPeakCappedUnits.multiply(plan.getOffPeakElecRateInclAll());
    	            	}
    	            	offPeakNonCappedCosts = offPeakNonCappedUnits.multiply(plan.getOffPeakElecRateInclAll());
    	            	
    	            	offPeakTotalCosts = offPeakCappedCosts.add(offPeakNonCappedCosts);
    	            	  	
    	            	plan.setELECTRICTY_USAGE_OFF_PEAK_IN_KWH(usageOffPeak);
    	                
    	                plan.setOffPeakElecYearlyCostsInclAll(offPeakTotalCosts);
    	                
    	                plan.addElecTotalYearlyCostsInclAll(offPeakTotalCosts);
    	            	
    	            
    	      
    		    	}
    	            

    	            if (originalUsageOnPeak.compareTo(BigDecimal.ZERO) > 0 || originalUsageOffPeak.compareTo(BigDecimal.ZERO) > 0) {
    	            	    	
    	            	plan.setElecFixedDailyCosts(container.getElectricityFixedCosts()); 
    	            	plan.setElecFixedDailyCostsInclTax(energyTaxService.fixedCostsAddTax(container.getElectricityFixedCosts()));
    	            	plan.setElecFixedMonthlyCostsInclTax(energyTaxService.numDailyToMonthly(plan.getElecFixedDailyCostsInclTax()));
    	            	plan.setElecFixedYearlyCostsInclTax(energyTaxService.numDailyToYearly(plan.getElecFixedDailyCostsInclTax()));

    	            	plan.setDistributorElecFixedDailyCosts(distributorService.getElecRate(DISTRIBUTOR));
    	            	plan.setDistributorElecFixedDailyCostsInclTax(energyTaxService.fixedCostsAddTax(distributorService.getElecRate(DISTRIBUTOR)));
    	            	plan.setDistributorElecFixedMonthlyCostsInclTax(energyTaxService.numDailyToMonthly(plan.getDistributorElecFixedDailyCostsInclTax()));
    	            	plan.setDistributorElecFixedYearlyCostsInclTax(energyTaxService.numDailyToYearly(plan.getDistributorElecFixedDailyCostsInclTax()));
    	            	
    	            	

    	            	plan.addElecTotalYearlyCostsInclAll(plan.getElecFixedYearlyCostsInclTax());
    	            	plan.addElecTotalYearlyCostsInclAll(plan.getDistributorElecFixedYearlyCostsInclTax());
    	            	plan.removeElecTotalYearlyCostsInclAll(energyTaxService.getElecTaxDiscount());
    	            	
    	            	
    	            	plan.addTotalYearlyCostsInclAll(plan.getElecTotalYearlyCostsInclAll());
    	            	plan.setElecTotalMonthlyInclAll(energyTaxService.numYearlyToMonthly(plan.getElecTotalYearlyCostsInclAll()));
    	            	
    	            }

    	            
    	            if (originalUsageGas.compareTo(BigDecimal.ZERO) > 0) {
    		    		
    		    		plan.setUsageGas(usageGas);
    	            	
    		    		plan.setGasRate(container.getGasRatePerM3());
    		    		plan.setGasRateInclAll(energyTaxService.gasRateAddAllTax((container.getGasRatePerM3()))); 
    		    		
    	           
    	            	BigDecimal gasCappedUnits = energyTaxService.calculateCappedUnits(usageGas, usageGas, new BigDecimal(1200));
    	            	BigDecimal gasNonCappedUnits = energyTaxService.calculateNonCappedUnits(usageGas, usageGas, new BigDecimal(1200));
    	         

    	            	BigDecimal gasCappedCosts = BigDecimal.ZERO;
    	            	BigDecimal gasNonCappedCosts = BigDecimal.ZERO;
    	            	BigDecimal gasTotalCosts = BigDecimal.ZERO;
    	            	
    	            	if(plan.getGasRateInclAll().compareTo(m3MaxPrice) >= 0) {
    	            	    gasCappedCosts = gasCappedUnits.multiply(m3MaxPrice);
    	            	} else {
    	            	    gasCappedCosts = gasCappedUnits.multiply(plan.getGasRateInclAll());
    	            	}
    	            	gasNonCappedCosts = gasNonCappedUnits.multiply(plan.getGasRateInclAll());
    	            	
    	            	gasTotalCosts = gasCappedCosts.add(gasNonCappedCosts);
    	            	  	
    	            	plan.setGAS_USAGE_IN_M3(usageGas);
    	                
    	                plan.setGasYearlyCostsInclAll(gasTotalCosts);
    	                
    	                plan.addGasTotalYearlyCostsInclAll(gasTotalCosts);
    		    		
    		    		
    		    		
    		    	
    		    		//BigDecimal gasDailyUsage = energyTaxService.numYearlyToDaily(new BigDecimal(GAS_USAGE_IN_M3.get()));
    	            	//plan.setGasDailyCostsInclAll(gasDailyUsage.multiply(plan.getSolarProductionRateInclAll()));
    	            	//plan.setGasMonthlyCostsInclAll(energyTaxService.numDailyToMonthly(plan.getGasDailyCostsInclAll()));
    	            	
    		    		
    		    		plan.setGasFixedDailyCosts(container.getGasFixedCosts());
    		    		plan.setGasFixedDailyCostsInclTax(energyTaxService.fixedCostsAddTax(container.getGasFixedCosts()));
    		    		plan.setGasFixedMonthlyCostsInclTax(energyTaxService.numDailyToMonthly(plan.getGasFixedDailyCostsInclTax()));
    		    		plan.setGasFixedYearlyCostsInclTax(energyTaxService.numDailyToYearly(plan.getGasFixedDailyCostsInclTax()));
    		    		
    		    		plan.setDistributorGasFixedDailyCosts(distributorService.getGasRate(DISTRIBUTOR));
    		    		plan.setDistributorGasFixedDailyCostsInclTax(energyTaxService.fixedCostsAddTax(distributorService.getGasRate(DISTRIBUTOR)));
    		    		plan.setDistributorGasFixedMonthlyCostsInclTax(energyTaxService.numDailyToMonthly(plan.getDistributorGasFixedDailyCostsInclTax()));
    		    		plan.setDistributorGasFixedYearlyCostsInclTax(energyTaxService.numDailyToYearly(plan.getDistributorGasFixedDailyCostsInclTax()));
    	            	
    		    		
    		    		
    		    		plan.addGasTotalYearlyCostsInclAll(plan.getGasFixedYearlyCostsInclTax());
    	            	plan.addGasTotalYearlyCostsInclAll(plan.getDistributorGasFixedYearlyCostsInclTax());
    	            	
    	            	
    	            	plan.addTotalYearlyCostsInclAll(plan.getGasTotalYearlyCostsInclAll());
    	            	//plan.removeGasTotalYearlyCostsInclAll(energyTaxService.getElecTaxDiscount());
    		    	}
    	            
    	            
    	            plan.setTotalMonthlyCostsInclAll(energyTaxService.numYearlyToMonthly(plan.getTotalYearlyCostsInclAll()));
    	            
    	            
    	            plans.add(plan);
    	        }
    	        plansByDuration.put(durationCategory, plans);
    	    }

    	    return plansByDuration;
	    
	}


    public BigDecimal calculateRate() {
    	return null;
    }
}
