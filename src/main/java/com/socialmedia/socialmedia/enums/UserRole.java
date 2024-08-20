package com.socialmedia.socialmedia.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public enum UserRole {
    ADMIN("ADMIN"), USER("USER");

    private final String type;

}
