package com.socialmedia.socialmedia.repositories;

import com.socialmedia.socialmedia.entities.Chat;
import com.socialmedia.socialmedia.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("SELECT c FROM Chat c WHERE c.isGroup = false AND :user MEMBER OF c.users AND :reqUser MEMBER OF c.users")
    Chat findSingleChatByUserIds(@Param("user") User user, @Param("reqUser") User reqUser);
//
//    @Query("SELECT c FROM Chat c JOIN c.users u1 JOIN c.users u2 WHERE c.isGroup = false AND u1 = :user AND u2 = :reqUser")
//    List<Chat> findSingleChatByUserIds(@Param("user") User user, @Param("reqUser") User reqUser);

    @Query("SELECT c FROM Chat c JOIN c.users u WHERE u.id =:userId")
    List<Chat> findChatByUserId(@Param("userId") Long userId);

//    @Query("SELECT c FROM Chat c WHERE :user1 MEMBER OF c.users AND :user2 MEMBER OF c.users")
//    Optional<Chat> findByUsers(@Param("user1") Long user1Id, @Param("user2") Long user2Id);

    @Query("SELECT c FROM Chat c WHERE :user1 MEMBER OF c.users AND :user2 MEMBER OF c.users")
    Optional<Chat> findByUsers(@Param("user1") User user1, @Param("user2") User user2);

}
