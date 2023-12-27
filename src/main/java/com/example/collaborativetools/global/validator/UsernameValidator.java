package com.example.collaborativetools.global.validator;

import com.example.collaborativetools.global.annotation.Username;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.text.MessageFormat;

public class UsernameValidator implements ConstraintValidator<Username, String> { // 1
    private static final int MIN_SIZE = 2;
    private static final int MAX_SIZE = 10;
    private static final String regexUsername = "^(?=.*[a-z0-9가-힣])[a-z0-9가-힣]{" + MIN_SIZE + "," + MAX_SIZE + "}+$";


    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        boolean isValidUsername = username.matches(regexUsername);

        if (!isValidUsername) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    MessageFormat.format("username은 {0}자 이상의 {1}자 이하의 영어 또는 숫자 또는 한글을 입력해주세요.",
                            MIN_SIZE,
                            MAX_SIZE)).addConstraintViolation();
        }
        return isValidUsername;
    }
}