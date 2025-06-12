package com.synassignment01.exceptions;

/**
 * Custom exception thrown when the provided passwords do not match during user registration.
 */
public class PasswordMismatchException extends RuntimeException{
    public  PasswordMismatchException(){
        super("Passwords don't match, please check");
    }
}
