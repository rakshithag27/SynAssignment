package com.synassignment01.repository;

import com.synassignment01.model.CloudinaryInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for performing CRUD operations on CloudinaryInfo entities.
 * Extends JpaRepository to provide default methods for database interaction.
 */
@Repository
public interface CloudinaryRepository extends JpaRepository<CloudinaryInfo,Long> {
    CloudinaryInfo findByPublicId(String publicId);
    List<CloudinaryInfo> findAllByUserUsername(String username);
}
