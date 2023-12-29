package com.example.collaborativetools.redis.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Getter;

@Getter
public class RefreshToken {
    @Id
    private String username;

    @Column
    private String token;
}
