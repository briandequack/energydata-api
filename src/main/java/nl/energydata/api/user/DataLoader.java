package nl.energydata.api.user;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements ApplicationRunner {
	
    @Value("${property.SUPER_USERNAME}")
    private String username;
    
    @Value("${property.SUPER_PASSWORD}")
    private String password;

    @Autowired private RoleRepository roleRepository;
    
    @Autowired private UserRepository userRepository;
    
    @Autowired private PasswordEncoder passwordEncoder;

    public void run(ApplicationArguments args) {
        if (roleRepository.count() == 0) {
        	Role adminRole = new Role();
        	adminRole.setName(RoleName.ROLE_ADMIN);
        	
        	Role userRole = new Role();
        	userRole.setName(RoleName.ROLE_USER);
        	roleRepository.saveAll(Arrays.asList(adminRole, userRole));
      
            User adminUser = new User();
            adminUser.setUsername(username);
            adminUser.setPassword(passwordEncoder.encode(password)); 
            adminUser.getRoles().add(adminRole);
            userRepository.save(adminUser);
        }
    }
}
