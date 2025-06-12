package com.synassignment01.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity class representing metadata for images uploaded to Cloudinary.
 * Stores the image URL, Cloudinary public ID, associated user, and upload timestamp.
 */
@Getter
@Setter
@Entity
@Table(name = "user_image")
public class CloudinaryInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    private String imageUrl;
    private String publicId;
    private boolean deleted = false;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserInfo user;

    private LocalDateTime uploadedAt = LocalDateTime.now();
}
