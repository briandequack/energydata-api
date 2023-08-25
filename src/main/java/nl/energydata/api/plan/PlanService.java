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
    	
    	Map<DurationCategory, List<EnergyUtilityDataContainer>> result = energyUtilityDataContainerService.getByUtilityRates(
    			ELECTRICTY_USAGE_ON_PEAK_IN_KWH,
    			ELECTRICTY_USAGE_OFF_PEAK_IN_KWH,
    			ELECTRICTY_PRODUCTION_IN_KWH,
    			GAS_USAGE_IN_M3,PAGE_NUMBER,
    			PAGE_SIZE);
    	
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
    	            BigDecimal usageOnPeak = originalUsageOnPeak;
    	            BigDecimal usageOffPeak = originalUsageOffPeak;
    	            BigDecimal originalUsageGas = GAS_USAGE_IN_M3.map(BigDecimal::valueOf).orElse(BigDecimal.ZERO);
    	            BigDecimal usageGas = originalUsageGas;
    	            BigDecimal solarProduction = ELECTRICTY_PRODUCTION_IN_KWH.map(BigDecimal::valueOf).orElse(BigDecimal.ZERO);
    	            BigDecimal solarProductionRemaining = ELECTRICTY_PRODUCTION_IN_KWH.map(BigDecimal::valueOf).orElse(BigDecimal.ZERO);
    	            
    	            BigDecimal totalUsageInKwh =  usageOnPeak.add(usageOffPeak);
    	            
    	            BigDecimal discountCapKwh = new BigDecimal(2900);
    	            BigDecimal discountCapM3 = new BigDecimal(1200);
    	            BigDecimal kwhMaxPrice =  new BigDecimal(0.4);
    	            BigDecimal m3MaxPrice =  new BigDecimal(1.45);
    	            
    	            if (solarProduction.compareTo(BigDecimal.ZERO) > 0) {
    	            	BigDecimal[] results;

	            	    results = distributeSolarProduction(BigDecimal.valueOf(0.714), usageOnPeak, solarProduction, solarProductionRemaining);
	            	    usageOnPeak = results[0];
	            	    solarProductionRemaining = results[1];

	            	    results = distributeSolarProduction(BigDecimal.valueOf(0.286), usageOffPeak, solarProduction, solarProductionRemaining);
	            	    usageOffPeak = results[0];
	            	    solarProductionRemaining = results[1];
	                
	            	    results = distributeRemainingSolarProduction(usageOnPeak, usageOffPeak, solarProductionRemaining);
	            	    usageOnPeak = results[0];
	            	    usageOffPeak = results[1];
	            	    solarProductionRemaining = results[2];
    	                
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
       	            	BigDecimal onPeakCappedUnits = energyTaxService.calculateCappedUnits(usageOnPeak, totalUsageInKwh, discountCapKwh);
    	            	BigDecimal onPeakNonCappedUnits = energyTaxService.calculateNonCappedUnits(usageOnPeak, totalUsageInKwh, discountCapKwh);
    	            	BigDecimal onPeakTotalCosts = calculateCosts(plan.getOnPeakElecRateInclAll(), onPeakCappedUnits, onPeakNonCappedUnits, kwhMaxPrice);
    	                plan.setOnPeakElecYearlyCostsInclAll(onPeakTotalCosts);
      	                plan.addElecTotalYearlyCostsInclAll(onPeakTotalCosts);
      	            }

    	            if (originalUsageOffPeak.compareTo(BigDecimal.ZERO) > 0) {	            	
    	            	plan.setOffPeakElecUsage(usageOffPeak);
    	            	plan.setOffPeakElecRate(container.getElectricityOffPeakRatePerKwh());
    	            	plan.setOffPeakElecRateInclAll(energyTaxService.elecRateAddAllTax((container.getElectricityOffPeakRatePerKwh())));
    	            	BigDecimal offPeakCappedUnits = energyTaxService.calculateCappedUnits(usageOffPeak, totalUsageInKwh, discountCapKwh);
    	            	BigDecimal offPeakNonCappedUnits = energyTaxService.calculateNonCappedUnits(usageOffPeak, totalUsageInKwh, discountCapKwh);
    	            	BigDecimal offPeakTotalCosts = calculateCosts(plan.getOffPeakElecRateInclAll(), offPeakCappedUnits, offPeakNonCappedUnits, kwhMaxPrice);
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
    		    		BigDecimal gasCappedUnits = energyTaxService.calculateCappedUnits(usageGas, usageGas, discountCapM3);
    	            	BigDecimal gasNonCappedUnits = energyTaxService.calculateNonCappedUnits(usageGas, usageGas, discountCapM3);
    	            	BigDecimal gasTotalCosts = calculateCosts(plan.getGasRateInclAll(), gasCappedUnits, gasNonCappedUnits, m3MaxPrice);
    	                plan.setGasYearlyCostsInclAll(gasTotalCosts);
    	                plan.addGasTotalYearlyCostsInclAll(gasTotalCosts);
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
    		    	}
    	            
    	     
       
    	            plan.setTotalMonthlyCostsInclAll(energyTaxService.numYearlyToMonthly(plan.getTotalYearlyCostsInclAll())); 
    	            plans.add(plan);
    	        }
    	        
    	        plansByDuration.put(durationCategory, plans);
    	    }

    	    return plansByDuration;
	    
	}


	private BigDecimal[] distributeSolarProduction(BigDecimal solarProductionRatio, BigDecimal usage, BigDecimal solarProduction, BigDecimal solarProductionRemaining) {
	    BigDecimal solarProductionPortion = solarProduction.multiply(solarProductionRatio);

	    if (solarProductionPortion.compareTo(usage) > 0) {
	        solarProductionRemaining = solarProductionRemaining.subtract(usage);
	        usage = BigDecimal.ZERO;
	    } else {
	        usage = usage.subtract(solarProductionPortion);
	        solarProductionRemaining = solarProductionRemaining.subtract(solarProductionPortion);
	    }

	    return new BigDecimal[]{usage, solarProductionRemaining};
	}
	
	
	private BigDecimal[] distributeRemainingSolarProduction(BigDecimal usageOnPeak, BigDecimal usageOffPeak, BigDecimal solarProductionRemaining) {
	    if (solarProductionRemaining.compareTo(BigDecimal.ZERO) <= 0) return new BigDecimal[]{usageOnPeak, usageOffPeak, solarProductionRemaining};

	    BigDecimal[] results;
	    if (usageOnPeak.compareTo(BigDecimal.ZERO) > 0) {
	        results = distributeSolarProduction(BigDecimal.ONE, usageOnPeak, solarProductionRemaining, solarProductionRemaining);
	        usageOnPeak = results[0];
	        solarProductionRemaining = results[1];
	    } else if (usageOffPeak.compareTo(BigDecimal.ZERO) > 0) {
	        results = distributeSolarProduction(BigDecimal.ONE, usageOffPeak, solarProductionRemaining, solarProductionRemaining);
	        usageOffPeak = results[0];
	        solarProductionRemaining = results[1];
	    }

	    return new BigDecimal[]{usageOnPeak, usageOffPeak, solarProductionRemaining};
	}
	
	private BigDecimal calculateCosts(BigDecimal rateInclAll, BigDecimal cappedUnits, BigDecimal nonCappedUnits, BigDecimal maxPrice) {
	    BigDecimal cappedCosts;

	    if (rateInclAll.compareTo(maxPrice) >= 0) {
	        cappedCosts = cappedUnits.multiply(maxPrice);
	    } else {
	        cappedCosts = cappedUnits.multiply(rateInclAll);
	    }

	    BigDecimal nonCappedCosts = nonCappedUnits.multiply(rateInclAll);
	    BigDecimal totalCosts = cappedCosts.add(nonCappedCosts);
	    return totalCosts;
	}
}
