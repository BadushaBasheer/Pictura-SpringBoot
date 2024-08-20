package com.socialmedia.socialmedia.repositories;

import com.socialmedia.socialmedia.entities.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowerIdAndFollowedId(Long followerId, Long followedId);

    List<Follow> findByFollowerId(Long userId);

    List<Follow> findByFollowedId(Long userId);

    Optional<Follow> findByFollowerIdAndFollowedId(Long followerId, Long followedId);
}
