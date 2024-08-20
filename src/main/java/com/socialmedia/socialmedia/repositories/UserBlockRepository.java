package com.socialmedia.socialmedia.repositories;

import com.socialmedia.socialmedia.entities.UserBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserBlockRepository extends JpaRepository<UserBlock, Long> {

    Optional<UserBlock> findByBlockerIdAndBlockedId(Long blockerId, Long blockedId);
}
