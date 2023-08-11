package nl.energydata.api.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
	
	//@Autowired private PasswordEncoder passwordEncoder;
	
	//@Autowired private UserRepository userRepository;
	
	/*
	@GetMapping("/create")
    public String addNewUser(@RequestBody User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		userRepository.save(user);
		return "User added to database";
    }*/
	
	@GetMapping("/admin")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String testAdminAccess() {
		return "This endpoint is for admin only.";
    }
	
	@GetMapping("/anyone")
    public String testAnyoneAccess() {
		return "This endpoint is for anyone.";
    }
	
}
