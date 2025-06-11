package com.synassignment01.exceptions;

public class PasswordMismatchException extends RuntimeException{
    public  PasswordMismatchException(){
        super("Passwords don't match, please check");
    }
}
