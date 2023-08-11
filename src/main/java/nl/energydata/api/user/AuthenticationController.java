package nl.energydata.api.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/authenticate")
public class AuthenticationController {
	
	@Autowired private JwtService jwtService;
	
	@Autowired AuthenticationManager authenticationManager;
	
	@PostMapping()
	public String authenticateAndGetToken(@RequestBody AuthenticationRequest authenticationRequest) {
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
		if(authentication.isAuthenticated()) {
			return jwtService.generateToken(authenticationRequest.getUsername());
		} else {
			throw new UsernameNotFoundException("Invalid username or password.");
		}
	}
}
