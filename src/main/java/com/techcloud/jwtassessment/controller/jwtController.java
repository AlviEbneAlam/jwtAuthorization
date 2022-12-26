package com.techcloud.jwtassessment.controller;

import com.techcloud.jwtassessment.config.JwtTokenUtil;
import com.techcloud.jwtassessment.service.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("${api.base_url}")
public class jwtController {

    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    public jwtController(JwtTokenUtil jwtTokenUtil){
        this.jwtTokenUtil=jwtTokenUtil;
    }

    @GetMapping("/get")
    public ResponseEntity<String> getController(){
        System.out.println(jwtTokenUtil.getClaims().get("name"));
        System.out.println(jwtTokenUtil.getClaims().get("id"));
        System.out.println(jwtTokenUtil.getClaims().get("validated"));

        return new ResponseEntity<String>("Book", HttpStatus.OK);
    }

    @GetMapping("/getAll")
    public ResponseEntity<String> getAllController(){
        System.out.println(jwtTokenUtil.getClaims().get("name"));
        System.out.println(jwtTokenUtil.getClaims().get("id"));
        System.out.println(jwtTokenUtil.getClaims().get("validated"));

        return new ResponseEntity<String>(new ArrayList<>(Arrays.asList("Pen", "Book")).toString(), HttpStatus.OK);
    }




}
