package com.socialmedia.socialmedia.repositories;

import com.socialmedia.socialmedia.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
}

