package com.techcloud.jwtassessment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techcloud.jwtassessment.config.JwtTokenUtil;
import com.techcloud.jwtassessment.model.JwtRequest;
import com.techcloud.jwtassessment.model.JwtResponse;
import com.techcloud.jwtassessment.service.JwtUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("${api.base_url}")
@Slf4j
public class JwtAuthenticationController {

	@Autowired
	private AuthenticationManager authenticationManager;

	private JwtTokenUtil jwtTokenUtil;
	private JwtUserDetailsService jwtUserDetailsService;

	@Autowired
	public JwtAuthenticationController(JwtTokenUtil jwtTokenUtil,
									   JwtUserDetailsService jwtUserDetailsService){
		this.jwtTokenUtil=jwtTokenUtil;
		this.jwtUserDetailsService=jwtUserDetailsService;
	}

	@PostMapping(value = "/authenticate")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest jwtRequest)
			throws Exception {

		log.info(new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(jwtRequest));
		log.info("The entry is here");

		final UserDetails userDetails = jwtUserDetailsService
				.loadUserByUsername(jwtRequest.getName());

		final String token = jwtTokenUtil.generateToken(userDetails,jwtRequest);

		if(token!=null){
			JwtResponse jwtResponse=new JwtResponse("Success",token);
			log.info(new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(jwtResponse));
			return ResponseEntity.ok(jwtResponse);
		}
		else{
			JwtResponse jwtResponse=new JwtResponse("Success",token);
			log.info(new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(jwtResponse));
			return ResponseEntity.ok(jwtResponse);
		}


	}

	private void authenticate(String username, String password) throws Exception {
		Objects.requireNonNull(username);
		Objects.requireNonNull(password);

		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}
	}
}
