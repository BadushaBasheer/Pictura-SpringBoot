package com.socialmedia.socialmedia.repositories;

import com.socialmedia.socialmedia.entities.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findAdminByEmail(String email);

    boolean existsByEmail(String email);
}
