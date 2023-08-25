package nl.energydata.api.plan;

import java.math.BigDecimal;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import nl.energydata.library.datacontainer.DurationCategory;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Plan {
	
	// REQUEST DATA
	private BigDecimal ELECTRICTY_USAGE_ON_PEAK_IN_KWH;
	private BigDecimal ELECTRICTY_USAGE_OFF_PEAK_IN_KWH;
	private BigDecimal ELECTRICTY_PRODUCTION_IN_KWH;
	private BigDecimal GAS_USAGE_IN_M3;
	private String DISTRIBUTOR;
	
	// PLAN DATA
	private String energyDataProviderName;
	private DurationCategory contractDuration;
	
	//ON PEAK ELEC RATES
	private BigDecimal onPeakElecUsage;
	private BigDecimal onPeakElecRate;
	private BigDecimal onPeakElecRateInclAll;
	private BigDecimal onPeakElecDailyCostsInclAll;
	private BigDecimal onPeakElecMonthlyCostsInclAll;
	private BigDecimal onPeakElecYearlyCostsInclAll;
	
	
	//OFF PEAK ELEC RATES
	private BigDecimal offPeakElecRate;
	private BigDecimal offPeakElecUsage;
	private BigDecimal offPeakElecRateInclAll;
	private BigDecimal offPeakElecDailyCostsInclAll;
	private BigDecimal offPeakElecMonthlyCostsInclAll;
	private BigDecimal offPeakElecYearlyCostsInclAll;
	
	
	//SOLAR
	private BigDecimal solarProductionRate;
	private BigDecimal solarProductionRateInclAll;
	private BigDecimal solarProductionDailyProfitsInclAll;
	private BigDecimal solarProductionMonthlyProfitsInclAll;
	
		
	// ELEC FIXED COSTS
	private BigDecimal elecFixedDailyCosts;
	private BigDecimal elecFixedDailyCostsInclTax;
	private BigDecimal elecFixedMonthlyCostsInclTax;
	private BigDecimal elecFixedYearlyCostsInclTax;
	
		
	// ELEC DISTRIBUTOR COST
	private BigDecimal distributorElecFixedDailyCosts;
	private BigDecimal distributorElecFixedDailyCostsInclTax;
	private BigDecimal distributorElecFixedMonthlyCostsInclTax;
	private BigDecimal distributorElecFixedYearlyCostsInclTax;
	
	// TAX DISCOUNT
	private BigDecimal elecTaxDiscountDaily;
	private BigDecimal elecTaxDiscountMonthly;
	private BigDecimal elecTaxDiscountYearly;
	
	
	// ELEC TOTAL
	private BigDecimal elecTotalMonthlyInclAll = BigDecimal.ZERO;
	private BigDecimal elecTotalYearlyCostsInclAll = null;

	

	// GAS
	private BigDecimal usageGas;
	private BigDecimal gasRate;
	private BigDecimal gasRateInclAll;
	private BigDecimal gasDailyCostsInclAll;
	private BigDecimal gasMonthlyCostsInclAll;
	private BigDecimal gasYearlyCostsInclAll;
	
	// GAS FIXED COSTS
	private BigDecimal gasFixedDailyCosts;
	private BigDecimal gasFixedDailyCostsInclTax;
	private BigDecimal gasFixedMonthlyCostsInclTax;
	private BigDecimal gasFixedYearlyCostsInclTax;
	
	// GAS DISTRIBUTOR COSTS
	private BigDecimal distributorGasFixedDailyCosts;
	private BigDecimal distributorGasFixedDailyCostsInclTax;
	private BigDecimal distributorGasFixedMonthlyCostsInclTax;
	private BigDecimal distributorGasFixedYearlyCostsInclTax;
	
	
	//TOTAL CONTRACT
	private BigDecimal totalYearlyCostsInclAll = BigDecimal.ZERO;
	private BigDecimal totalMonthlyCostsInclAll  = BigDecimal.ZERO;
	
	
	// ELEC TOTAL
	private BigDecimal gasTotalYearlyCostsInclAll = null;
	

	public void addElecTotalYearlyCostsInclAll(BigDecimal num) {
		if(this.elecTotalYearlyCostsInclAll == null) {
			this.elecTotalYearlyCostsInclAll = BigDecimal.ZERO;
		}
		this.elecTotalYearlyCostsInclAll = elecTotalYearlyCostsInclAll.add(num);
	}
	
	public void removeElecTotalYearlyCostsInclAll(BigDecimal num) {
		this.elecTotalYearlyCostsInclAll = elecTotalYearlyCostsInclAll.subtract(num);
	}
	
	
	public void addGasTotalYearlyCostsInclAll(BigDecimal num) {
		if(this.gasTotalYearlyCostsInclAll == null) {
			this.gasTotalYearlyCostsInclAll = BigDecimal.ZERO;
		}
		this.gasTotalYearlyCostsInclAll = gasTotalYearlyCostsInclAll.add(num);
	}
	
	public void removeGasTotalYearlyCostsInclAll(BigDecimal num) {
		this.gasTotalYearlyCostsInclAll = gasTotalYearlyCostsInclAll.subtract(num);
	}
	
	public void addTotalYearlyCostsInclAll(BigDecimal num) {
		this.totalYearlyCostsInclAll = totalYearlyCostsInclAll.add(num);
	}

}
