package com.socialmedia.socialmedia.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.socialmedia.socialmedia.enums.Status;
import com.socialmedia.socialmedia.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true)
    private String name;

    @Column(unique = true, length = 100)
    private String email;

    private String password;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    private boolean accountLocked;

    private String bio;

    private String profilePic;

    private String backgroundImage;

    private boolean enabled;

    private boolean isBlockedByAdmin;

    private boolean isGoogleSignIn;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

//    @Enumerated(EnumType.STRING)
//    private Status status;

    @OneToMany(mappedBy = "blocker", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserBlock> blockedUsers = new HashSet<>();

    @OneToMany(mappedBy = "blocked", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserBlock> blockedByUsers = new HashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "followed", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Follow> followers = new HashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Follow> following = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_saved_posts",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id")
    )
    @JsonIgnore
    private List<Post> savedPost = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(this.userRole.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked && !isBlockedByAdmin;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
