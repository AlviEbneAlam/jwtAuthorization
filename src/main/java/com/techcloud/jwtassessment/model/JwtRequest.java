package com.techcloud.jwtassessment.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class JwtRequest implements Serializable {
	
	private String name;
	private String id;
	private String validated;

}