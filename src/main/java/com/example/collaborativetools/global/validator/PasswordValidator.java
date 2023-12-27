package com.example.collaborativetools.global.validator;

import com.example.collaborativetools.global.annotation.Password;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.text.MessageFormat;

public class PasswordValidator implements ConstraintValidator<Password, String> {
    private static final int MIN_SIZE = 4;
    private static final int MAX_SIZE = 15;
    private static final String regex = "(?=.*[0-9])(?=.*[a-zA-Z])[a-zA-Z0-9!@#$%^&*()._-]{" + MIN_SIZE + "," + MAX_SIZE + "}+$";


    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        boolean isValidPassword = password.matches(regex);
        if (!isValidPassword) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    MessageFormat.format("password는 {0}자 이상의 {1}자 이하의 영어,숫자를 반드시 포함한 영어,숫자,특수문자의 조합을 입력해주세요",
                            MIN_SIZE,
                            MAX_SIZE)).addConstraintViolation();
        }
        return isValidPassword;
    }
}
