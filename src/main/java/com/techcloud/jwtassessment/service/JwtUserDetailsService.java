package com.techcloud.jwtassessment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@Slf4j
public class JwtUserDetailsService implements UserDetailsService {
	@Value("${jwt.username}")
	private String jwtUsername;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.info("Username in loadUserByUsername class is: "+username);
		if (jwtUsername.equals(username)) {
			return new User("Alvi Ibne Alam", "$2a$10$slYQmy",
					new ArrayList<>());
		} else {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
	}

}