package com.synassignment01.exceptions;

public class UserAlreadyExistsException extends RuntimeException{
    public UserAlreadyExistsException(String username){
        super("A user already exists with the username " +username);
    }
}
