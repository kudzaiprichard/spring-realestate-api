package com.intela.realestatebackend.exceptions;

public class MissingAccessTokenException extends RuntimeException{
    public MissingAccessTokenException(String message){
        super(message);
    }
}
