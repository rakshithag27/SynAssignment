package com.synassignment01.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.synassignment01.dto.CloudinaryResponse;
import com.synassignment01.dto.CloudinaryViewResponse;
import com.synassignment01.exceptions.CloudinaryException;
import com.synassignment01.model.CloudinaryInfo;
import com.synassignment01.model.UserInfo;
import com.synassignment01.repository.CloudinaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service class responsible for handling Cloudinary image operations.
 * Provides functionality to upload, delete, and view images for authenticated users.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;
    private final CloudinaryRepository cloudinaryRepository;

    /**
     * Uploads an image to Cloudinary and saves the associated metadata to the database.
     *
     * @param file      the image file to upload
     * @param userInfo  the user uploading the image
     * @return a response containing the image URL and public ID
     * @throws CloudinaryException if a general error occurs during upload
     * @throws IOException if file conversion or Cloudinary upload fails
     */
    public CloudinaryResponse uploadImage(MultipartFile file, UserInfo userInfo) throws CloudinaryException, IOException {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            CloudinaryInfo imageInfo = new CloudinaryInfo();
            imageInfo.setPublicId(uploadResult.get("public_id").toString());
            imageInfo.setImageUrl(uploadResult.get("secure_url").toString());
            imageInfo.setUser(userInfo);
            cloudinaryRepository.save(imageInfo);
            return new CloudinaryResponse(uploadResult.get("secure_url").toString(), uploadResult.get("public_id").toString());
        } catch(IOException i) {
            log.error("Failed to upload Image : " + i.getMessage());
            throw new IOException(i.getMessage());
        } catch (Exception e) {
            log.error("Failed to upload Image : " + e.getMessage());
            throw new CloudinaryException(e.getMessage());
        }
    }

    /**
     * Deletes an image from Cloudinary and marks it as deleted in the database.
     *
     * @param publicId the unique public ID of the image to delete
     * @return a string response from Cloudinary (e.g., "ok")
     * @throws CloudinaryException if deletion fails due to business logic
     * @throws IOException if communication with Cloudinary fails
     */
    public String deleteImage(String publicId) throws IOException, CloudinaryException {
        try {
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            CloudinaryInfo imageInfo = cloudinaryRepository.findByPublicId(publicId);
            imageInfo.setDeleted(true);
            cloudinaryRepository.save(imageInfo);
            return result.get("result").toString();
        } catch(IOException i) {
            log.error("Failed to delete Image : " + i.getMessage());
            throw new IOException(i.getMessage());
        } catch (Exception e) {
            log.error("Failed to delete Image : " + e.getMessage());
            throw new CloudinaryException(e.getMessage());
        }
    }

    /**
     * Retrieves a list of image URLs for a specific user by username.
     *
     * @param username the username of the user whose images are being requested
     * @return a response containing a list of image URLs
     * @throws CloudinaryException if fetching images fails
     */
    public CloudinaryViewResponse viewImage(String username) throws CloudinaryException {
        try {
            List<CloudinaryInfo> dbResponse = cloudinaryRepository.findAllByUserUsername(username);
            List<String> responseUrls = new ArrayList<>();
            for (CloudinaryInfo cloudinaryInfo : dbResponse) {
                responseUrls.add(cloudinaryInfo.getImageUrl());
            }
            CloudinaryViewResponse viewResponse = new CloudinaryViewResponse(responseUrls);
            return viewResponse;
        } catch (Exception e) {
            log.error("Failed to view Image : " + e.getMessage());
            throw new CloudinaryException(e.getMessage());
        }
    }
}

