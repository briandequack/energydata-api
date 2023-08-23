package nl.energydata.api.plan;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nl.energydata.library.datacontainer.DurationCategory;


@RestController
@CrossOrigin(origins = {"http://localhost:3000", "https://enerwijs.nl", "https://www.enerwijs.nl"})
@RequestMapping("/api/v1/plan")
public class PlanController {
	
	@Autowired
	PlanService planService;
 	
	 @PostMapping()
	 public Map<DurationCategory, List<Plan>> calculatePlan(@RequestBody PlanRequest planRequest)  {
		 System.out.println(planRequest);
		return planService.calculate(planRequest);       
	}
}
