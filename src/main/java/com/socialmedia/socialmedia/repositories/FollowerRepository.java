package com.socialmedia.socialmedia.repositories;

import com.socialmedia.socialmedia.entities.Follower;
import com.socialmedia.socialmedia.entities.Following;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowerRepository extends JpaRepository<Follower, Long> {

    List<Follower> findUserByFollowerId(Long userId);

}
