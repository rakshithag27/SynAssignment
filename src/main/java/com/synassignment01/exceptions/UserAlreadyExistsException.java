package com.synassignment01.exceptions;

/**
 * Custom exception thrown when a user tries to register with a username that already exists.
 */
public class UserAlreadyExistsException extends RuntimeException{
    public UserAlreadyExistsException(String username){
        super("A user already exists with the username " +username);
    }
}
