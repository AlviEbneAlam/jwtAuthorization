package com.techcloud.jwtassessment.config;


import com.techcloud.jwtassessment.GeneralExceptions.ResourceNotFoundException;
import com.techcloud.jwtassessment.GeneralExceptions.UnauthorizedException;
import com.techcloud.jwtassessment.service.JwtUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

	private JwtUserDetailsService jwtUserDetailsService;
	private JwtTokenUtil jwtTokenUtil;

	private List<RequestMatcher> includedPathMatchers = new ArrayList<>();
	private List<RequestMatcher> excludedPathMatchers = new ArrayList<>();

	@Autowired
	public JwtRequestFilter(JwtUserDetailsService jwtUserDetailsService,
							JwtTokenUtil jwtTokenUtil){
		this.jwtUserDetailsService=jwtUserDetailsService;
		this.jwtTokenUtil=jwtTokenUtil;
	}

	public List<RequestMatcher> getIncludedPathMatchers() {
		return includedPathMatchers;
	}

	public void setIncludedPathMatchers(List<RequestMatcher> includedPathMatchers) {
		this.includedPathMatchers = includedPathMatchers;
	}

	public List<RequestMatcher> getExcludedPathMatchers() {
		return excludedPathMatchers;
	}

	public JSONObject formJson(String message,String path){
		JSONObject jwtParseError=new JSONObject();
		jwtParseError.put("timestamp", Instant.now());
		jwtParseError.put("status",HttpStatus.UNAUTHORIZED.getReasonPhrase());
		jwtParseError.put("message",message);
		jwtParseError.put("path",path);

		return jwtParseError;
	}

	public void setExcludedPathMatchers(List<RequestMatcher> excludedPathMatchers) {
		this.excludedPathMatchers = excludedPathMatchers;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request,
									HttpServletResponse response,
									FilterChain chain)
			throws IOException, ServletException {

		final String requestTokenHeader = request.getHeader("Authorization");

		String username = null;
		String jwtToken = null;

		// JWT Token is in the form "Bearer token". Remove Bearer word and get only the Token
		if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
			jwtToken = requestTokenHeader.substring(7);
			try {
				username = jwtTokenUtil.getUsernameFromToken(jwtToken);
			} catch (RuntimeException e) {
				System.out.println("Unable to get JWT Token");
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.setStatus(HttpStatus.UNAUTHORIZED.value());
				response.getWriter().write(formJson(e.getLocalizedMessage(),request.getServletPath()).toJSONString());
				return;
			} /*catch (ExpiredJwtException e) {
				System.out.println("JWT Token has expired");
			}*/
		} else {
			logger.warn("JWT Token does not begin with Bearer String");
		}

		//Once we get the token validate it.
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

			UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);

			// if token is valid configure Spring Security to manually set authentication
			try{
				if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {

					UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());
					usernamePasswordAuthenticationToken
							.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					// After setting the Authentication in the context, we specify
					// that the current user is authenticated. So it passes the Spring Security Configurations successfully.
					SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
				}
			}
			catch(RuntimeException resourceNotFoundException){
				response.setStatus(HttpStatus.UNAUTHORIZED.value());
				response.getWriter().write(resourceNotFoundException.getLocalizedMessage());
			}


		}
		chain.doFilter(request, response);

	}


}
