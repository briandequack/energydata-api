package nl.energydata.api.plan;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanRequest {
	
   @JsonProperty("ELECTRICTY_USAGE_ON_PEAK_IN_KWH")
    private Integer ELECTRICTY_USAGE_ON_PEAK_IN_KWH;
    
    @JsonProperty("ELECTRICTY_USAGE_OFF_PEAK_IN_KWH")
    private Integer ELECTRICTY_USAGE_OFF_PEAK_IN_KWH;
    
    @JsonProperty("ELECTRICTY_PRODUCTION_IN_KWH")
    private Integer ELECTRICTY_PRODUCTION_IN_KWH;
    
    @JsonProperty("GAS_USAGE_IN_M3")
    private Integer GAS_USAGE_IN_M3;
    
    @JsonProperty("DISTRIBUTOR")
    private String DISTRIBUTOR;
    
    @JsonProperty("PAGE_NUMBER")
    private Integer PAGE_NUMBER;
    
    @JsonProperty("PAGE_SIZE")
    private Integer PAGE_SIZE;
    
}
