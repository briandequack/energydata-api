package nl.energydata.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;



@SpringBootApplication(scanBasePackages = {"nl.energydata.api", "nl.energydata.library"})
@EntityScan(basePackages = {"nl.energydata.api", "nl.energydata.library"})
@EnableJpaRepositories(basePackages = {"nl.energydata.api","nl.energydata.library"})
@ComponentScan(basePackages = {"nl.energydata.api", "nl.energydata.library"})
public class EnergyDataApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(EnergyDataApiApplication.class, args);
	
	}

}
