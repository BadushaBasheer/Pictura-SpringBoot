package com.socialmedia.socialmedia.entities;

import com.socialmedia.socialmedia.enums.UserRole;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Admin implements UserDetails {

    @Id
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole roles;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(this.roles.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }


}
