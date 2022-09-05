package com.example.openbudget.repository;

import com.example.openbudget.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface AdminRepository extends JpaRepository<Admin,Integer> {
    /**
     * find by username
     * @param username
     * @return
     */
    Optional<Admin> findByUsername(String username);
}
