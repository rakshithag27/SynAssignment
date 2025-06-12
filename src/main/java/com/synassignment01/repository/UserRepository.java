package com.synassignment01.repository;

import com.synassignment01.model.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for UserInfo entity.
 * Provides methods to perform CRUD operations and custom queries.
 */
@Repository
public interface UserRepository extends JpaRepository<UserInfo,Integer> {
    UserInfo findByUsername(String username);


}
