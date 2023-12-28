package com.example.collaborativetools.user.dto;

import com.example.collaborativetools.user.annotation.Password;
import lombok.Getter;

@Getter
public class PasswordRequestDto {
    private String currentPassword;
    @Password
    private String newPassword;
    private String confirmPassword;
}
