package com.socialmedia.socialmedia.repositories;

import com.socialmedia.socialmedia.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

//    @Query("SELECT p FROM Post p WHERE p.user.id =:userId")
//    List<Post> findPostByUserId(@Param("userId") Long userId);

    List<Post> findPostByUserId(Long userId);

    Optional<Post> findByImage_FileName(String fileName);


}
