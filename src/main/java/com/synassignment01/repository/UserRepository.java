package com.synassignment01.repository;

import com.synassignment01.model.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserInfo,String> {
    UserInfo findByUsername(String username);


}
