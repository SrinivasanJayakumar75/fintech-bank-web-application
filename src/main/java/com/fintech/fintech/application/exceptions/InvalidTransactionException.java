package com.fintech.fintech.application.exceptions;

public class InvalidTransactionException extends RuntimeException {

    public InvalidTransactionException(String error){
        super(error);
    }
    
}
