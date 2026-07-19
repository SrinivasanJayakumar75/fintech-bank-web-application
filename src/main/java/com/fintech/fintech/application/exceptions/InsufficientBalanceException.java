package com.fintech.fintech.application.exceptions;

public class InsufficientBalanceException extends RuntimeException{

    public InsufficientBalanceException(String error){
        super(error);
    }
}
