package com.example.collaborativetools.user.dto;

import com.example.collaborativetools.user.entitiy.User;
import lombok.Getter;

@Getter
public class UserInfoDto {
    private Long id;
    private String username;

    public UserInfoDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
    }
}
