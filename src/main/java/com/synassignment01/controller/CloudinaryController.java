package com.synassignment01.controller;

import com.synassignment01.dto.CloudinaryResponse;
import com.synassignment01.exceptions.CloudinaryException;
import com.synassignment01.model.UserInfo;
import com.synassignment01.repository.UserRepository;
import com.synassignment01.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * REST controller for handling image operations with Cloudinary.
 * Supports image upload, deletion, and viewing for authenticated users.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/synassignment/images")
public class CloudinaryController {

    private final CloudinaryService cloudinaryService;
    private final UserRepository userRepository;

    /**
     * Uploads an image to Cloudinary and saves metadata to the database.
     *
     * @param file the image file to upload
     * @return the uploaded image details or an error message
     */
    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            UserInfo user = userRepository.findByUsername(username);
            CloudinaryResponse uploadResult = cloudinaryService.uploadImage(file, user);
            return ResponseEntity.ok(uploadResult);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed: " + e.getMessage());
        }
    }

    /**
     * Deletes an image from Cloudinary by its public ID.
     *
     * @param publicId the public ID of the image to delete
     * @return deletion result or an error message
     */
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteImage(@RequestParam String publicId) throws IOException, CloudinaryException {
        try {
            String result = cloudinaryService.deleteImage(publicId);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Image deletion failed: " + e.getMessage());
        }
    }

    /**
     * Retrieves a list of image URLs for a given user.
     *
     * @param username the username whose images to view
     * @return list of image URLs or an error message
     */
    @GetMapping("/view")
    public ResponseEntity<?> view(@RequestParam String username) {
        try{
            return ResponseEntity.ok(cloudinaryService.viewImage(username));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Image view failed: " + e.getMessage());
        }
    }
}
