package com.example.collaborativetools.user.service;

import com.example.collaborativetools.board.entitiy.Board;
import com.example.collaborativetools.board.repository.BoardRepository;
import com.example.collaborativetools.global.constant.ErrorCode;
import com.example.collaborativetools.global.constant.UserRoleEnum;
import com.example.collaborativetools.global.exception.ApiException;
import com.example.collaborativetools.user.dto.LoginRequestDto;
import com.example.collaborativetools.user.dto.PasswordRequestDto;
import com.example.collaborativetools.user.dto.SignupRequestDto;
import com.example.collaborativetools.user.dto.UserInfoDto;
import com.example.collaborativetools.user.entitiy.User;
import com.example.collaborativetools.user.repository.UserRepository;
import com.example.collaborativetools.userboard.entity.UserBoard;
import com.example.collaborativetools.userboard.repository.UserBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BoardRepository boardRepository;

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
        //해당유저가 관리자인 보드를 모두 없애버림
        List<UserBoard> userBoardList = user.getUserBoardList();
        for (UserBoard userBoard : userBoardList) {
            if (userBoard.getRole().equals(UserRoleEnum.ADMIN)) {
                Board board = boardRepository.findById(userBoard.getBoard().getId()).orElseThrow(
                        () -> new ApiException(ErrorCode.NOT_FOUND_BOARD)
                );
                boardRepository.delete(board);
            }
        }

        userRepository.delete(user);
    }

    public UserInfoDto getUserInfo(User loginUser) {
        User findUser = userRepository.findById(loginUser.getId()).orElseThrow(
                () -> new ApiException(ErrorCode.NOT_FOUND_USER)
        );

        return new UserInfoDto(findUser);
    }
}
