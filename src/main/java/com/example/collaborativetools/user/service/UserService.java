package com.example.collaborativetools.user.service;

import com.example.collaborativetools.global.constant.ErrorCode;
import com.example.collaborativetools.global.exception.ApiException;
import com.example.collaborativetools.user.dto.LoginRequestDto;
import com.example.collaborativetools.user.dto.PasswordRequestDto;
import com.example.collaborativetools.user.dto.SignupRequestDto;
import com.example.collaborativetools.user.entitiy.User;
import com.example.collaborativetools.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void signup(SignupRequestDto requestDto) {
        String username = requestDto.getUsername();
        String password = passwordEncoder.encode(requestDto.getPassword());

        Optional<User> duplicateUser = userRepository.findByUsername(username);
        if (duplicateUser.isPresent()) {
            throw new ApiException(ErrorCode.IS_DUPLICATE_USERNAME);
        }

        //비밀번호 확인 검사
        if (!requestDto.getPassword().equals(requestDto.getConfirmPassword())) {
            throw new ApiException(ErrorCode.NOT_EQUALS_CONFIRM_PASSWORD);
        }

        // 사용자 등록
        User user = new User(username, password);
        User savedUser = userRepository.save(user);
    }

    public void login(LoginRequestDto loginRequestDto) {
        String username = loginRequestDto.getUsername();
        String password = loginRequestDto.getPassword();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_USER));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ApiException(ErrorCode.NOT_EQUALS_PASSWORD);
        }

    }

    @Transactional
    public void updatePassword(PasswordRequestDto requestDto, User user) {
        String currentPassword = requestDto.getCurrentPassword();
        String newPassword = passwordEncoder.encode(requestDto.getNewPassword());

        //현재 비밀번호 확인
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new ApiException(ErrorCode.NOT_EQUALS_PASSWORD);
        }

        //새 비밀번호가 현재 비밀번호와 동일한지 확인 검사
        if (requestDto.getNewPassword().equals(currentPassword)) {
            throw new ApiException(ErrorCode.EQUALS_CURRENT_PASSWORD);
        }

        //비밀번호 확인 검사
        if (!requestDto.getNewPassword().equals(requestDto.getConfirmPassword())) {
            throw new ApiException(ErrorCode.NOT_EQUALS_CONFIRM_PASSWORD);
        }

        User loginUser = userRepository.findById(user.getId()).orElseThrow(
                () -> new ApiException(ErrorCode.NOT_FOUND_USER)
        );

        loginUser.updatePassword(newPassword);
    }
}
