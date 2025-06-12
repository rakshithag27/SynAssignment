package com.synassignment01.exceptions;

/**
 * Custom exception class for handling Cloudinary-related errors.
 * Thrown when operations like upload or delete fail.
 */
public class CloudinaryException extends Exception {
    /**
     * Constructs a new CloudinaryException with a detailed error message.
     *
     * @param message the specific reason for the exception
     */
    public  CloudinaryException(String message){
        super("Cloudinary Exception was thrown: " + message);
    }
}
