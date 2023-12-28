package com.example.collaborativetools.user.service;

import com.example.collaborativetools.global.constant.ErrorCode;
import com.example.collaborativetools.global.exception.ApiException;
import com.example.collaborativetools.user.dto.LoginRequestDto;
import com.example.collaborativetools.user.dto.PasswordRequestDto;
import com.example.collaborativetools.user.dto.SignupRequestDto;
import com.example.collaborativetools.user.dto.UserInfoDto;
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

    public UserInfoDto signup(SignupRequestDto requestDto) {
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

        return new UserInfoDto(savedUser);
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
    public void updatePassword(Long userId, PasswordRequestDto requestDto, User loginUser) {
        if (!userId.equals(loginUser.getId())) {
            throw new ApiException(ErrorCode.NOT_LOGIN_USER);
        }

        String currentPassword = requestDto.getCurrentPassword();
        String newPassword = passwordEncoder.encode(requestDto.getNewPassword());

        //현재 비밀번호 확인
        if (!passwordEncoder.matches(currentPassword, loginUser.getPassword())) {
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

        User findUser = userRepository.findById(userId).orElseThrow(
                () -> new ApiException(ErrorCode.NOT_FOUND_USER)
        );

        findUser.updatePassword(newPassword);
    }

    public void unregister(Long userId, User loginUser) {
        if (!userId.equals(loginUser.getId())) {
            throw new ApiException(ErrorCode.NOT_LOGIN_USER);
        }

        User user = userRepository.findById(userId).orElseThrow(
                () -> new ApiException(ErrorCode.NOT_FOUND_USER)
        );
        //연관관계 모두 끊어버리고 탈퇴해야함

        userRepository.delete(user);
    }

    public UserInfoDto getUserInfo(User loginUser) {
        User findUser = userRepository.findById(loginUser.getId()).orElseThrow(
                () -> new ApiException(ErrorCode.NOT_FOUND_USER)
        );

        return new UserInfoDto(findUser);
    }
}
