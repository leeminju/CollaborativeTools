package com.example.collaborativetools.user.controller;


import com.example.collaborativetools.global.dto.BaseResponse;
import com.example.collaborativetools.global.jwt.JwtUtil;
import com.example.collaborativetools.global.jwt.UserDetailsImpl;
import com.example.collaborativetools.redis.dto.TokenDto;
import com.example.collaborativetools.user.dto.LoginRequestDto;
import com.example.collaborativetools.user.dto.PasswordRequestDto;
import com.example.collaborativetools.user.dto.SignupRequestDto;
import com.example.collaborativetools.user.dto.UserInfoDto;
import com.example.collaborativetools.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.Token;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.example.collaborativetools.global.constant.ResponseCode.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/users/signup")
    public ResponseEntity<BaseResponse<UserInfoDto>> signup(@RequestBody @Valid SignupRequestDto requestDto) {
        UserInfoDto userInfoDto = userService.signup(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).
                body(BaseResponse.of(SIGNUP, userInfoDto));
    }

    @PostMapping("/users/login")
    public ResponseEntity<BaseResponse<String>> login(@RequestBody @Valid LoginRequestDto userRequestDto
            , HttpServletResponse response) {
        TokenDto tokenDto = userService.login(userRequestDto);

        jwtUtil.setTokenResponse(tokenDto, response);

        return ResponseEntity.ok()
                .body(BaseResponse.of(LOGIN, ""));
    }

    @PostMapping("/reissue")
    public ResponseEntity<BaseResponse<String>> reissue(HttpServletRequest request, HttpServletResponse response) {
        userService.reissue(request, response);
        return ResponseEntity.status(REISSUE_TOKEN.getHttpStatus())
                .body(
                        BaseResponse.of(
                                REISSUE_TOKEN,
                                ""
                        )
                );
    }

    @DeleteMapping("/users/logout")
    public ResponseEntity<BaseResponse<String>> logout(HttpServletRequest request) {
        userService.logout(request);
        return ResponseEntity.status(LOGOUT.getHttpStatus())
                .body(
                        BaseResponse.of(
                                LOGOUT,
                                ""
                        )
                );
    }

    // 로그인 사용자 정보 받기
    @GetMapping("/users")
    public ResponseEntity<BaseResponse<UserInfoDto>> getUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        UserInfoDto info = userService.getUserInfo(userDetails.getUser());
        return ResponseEntity.ok()
                .body(BaseResponse.of(GET_USER_INFO, info));
    }


    //비밀번호 변경
    @PutMapping("/users/{userId}/password")
    public ResponseEntity<BaseResponse<String>> updatePassword(@PathVariable Long userId, @RequestBody @Valid PasswordRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        userService.updatePassword(userId, requestDto, userDetails.getUser());
        return ResponseEntity.ok()
                .body(BaseResponse.of(UPDATE_PASSWORD, ""));
    }

    //회원 탈퇴
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<BaseResponse<String>> unregister(@PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        userService.unregister(userId, userDetails.getUser());
        return ResponseEntity.ok()
                .body(BaseResponse.of(UNREGISTER_USER, ""));
    }
}
