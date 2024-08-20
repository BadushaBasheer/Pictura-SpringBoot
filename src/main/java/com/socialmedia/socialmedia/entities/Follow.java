package com.socialmedia.socialmedia.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@EqualsAndHashCode
@Table(name = "follow")
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "followed_id")
    private User followed;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "follower_id")
    private User follower;
}