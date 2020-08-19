package com.nzp.wise2go.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nzp.wise2go.entities.User;
import com.nzp.wise2go.exception.ResourceNotFoundException;
import com.nzp.wise2go.repositories.UserRepository;



@Service
@Transactional
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(userName)
				.orElseThrow(() -> new UsernameNotFoundException("Username " + userName + " not found"));

		
		return UserPrincipal.create(user);
		
	}

    @Transactional
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("User", "id", id)
        );

        return UserPrincipal.create(user);
    }
    
	/*
	 * private static Collection<? extends GrantedAuthority> getAuthorities(User
	 * user) { String[] userRoles = user.getRoles().stream().map((role) ->
	 * role.getName()).toArray(String[]::new); Collection<GrantedAuthority>
	 * authorities = AuthorityUtils.createAuthorityList(userRoles);
	 * 
	 * 
	 * return authorities; }
	 */
}
