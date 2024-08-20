package com.socialmedia.socialmedia.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String chat_name;

    private String chat_image;

    @ManyToMany
    private Set<User> admins = new HashSet<>();

    @Column(name = "is_group")
    private boolean isGroup;

    @ManyToOne
    @JoinColumn(name = "crated_by")
    private User createdBy;


    @ManyToMany
    @JoinTable(name = "chat_users",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> users = new HashSet<>();


    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

}
