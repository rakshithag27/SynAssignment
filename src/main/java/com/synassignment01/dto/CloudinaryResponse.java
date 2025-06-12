package com.synassignment01.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO representing a Cloudinary upload response with imageUrl and publicId.
 */
@Getter
@Setter
@AllArgsConstructor
public class CloudinaryResponse {
    String imageUrl;
    String publicId;
}
