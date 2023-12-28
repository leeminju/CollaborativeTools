package com.example.collaborativetools.user.dto;

import com.example.collaborativetools.user.annotation.Password;
import com.example.collaborativetools.user.annotation.Username;
import lombok.Getter;

@Getter
public class LoginRequestDto {
    @Username
    private String username;
    @Password
    private String password;
}
