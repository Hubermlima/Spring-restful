package curso.api.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import curso.api.rest.model.UserSystem;
import curso.api.rest.repository.IUserSystem;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{

	@Autowired
	private IUserSystem iUserSystem;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		UserSystem userSystem = iUserSystem.findUserByLogin(username);
		
		if (userSystem == null) {
			throw new UsernameNotFoundException("Username not found!");
		}
		
		return new User(userSystem.getUsername(), userSystem.getPassword(), userSystem.getAuthorities());
	}

	public void inserirAcessoPadrao(Long id) {
		iUserSystem.inserirAcessoPadrao(id);
	}
}
