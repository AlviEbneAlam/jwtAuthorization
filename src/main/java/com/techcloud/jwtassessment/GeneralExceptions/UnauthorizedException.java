package com.techcloud.jwtassessment.GeneralExceptions;

public class UnauthorizedException extends RuntimeException{

    public UnauthorizedException(String message){
        super(message);
    }
}
