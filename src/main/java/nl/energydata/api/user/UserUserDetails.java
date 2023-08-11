package nl.energydata.api.user;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserUserDetails implements UserDetails{

    private String username;
    private String password;
    private List<GrantedAuthority> authorities;
    
    
    public UserUserDetails(User user) {
    	this.username = user.getUsername();
    	this.password = user.getPassword();
    	
    	this.authorities = user.getRoles().stream()
    		    .map(role -> new SimpleGrantedAuthority(role.getName().name()))
    		    .collect(Collectors.toList());
    }
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

}
