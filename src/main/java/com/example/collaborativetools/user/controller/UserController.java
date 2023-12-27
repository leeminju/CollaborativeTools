package com.example.collaborativetools.user.controller;


import com.example.collaborativetools.global.dto.BaseResponse;
import com.example.collaborativetools.global.jwt.JwtUtil;
import com.example.collaborativetools.global.jwt.UserDetailsImpl;
import com.example.collaborativetools.user.dto.LoginRequestDto;
import com.example.collaborativetools.user.dto.PasswordRequestDto;
import com.example.collaborativetools.user.dto.SignupRequestDto;
import com.example.collaborativetools.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<BaseResponse<String>> signup(@RequestBody @Valid SignupRequestDto requestDto) {
        userService.signup(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).
                body(BaseResponse.of(SIGNUP, ""));
    }

    @PostMapping("/users/login")
    public ResponseEntity<BaseResponse<String>> login(@RequestBody @Valid LoginRequestDto userRequestDto) {
        userService.login(userRequestDto);

        String token = jwtUtil.createToken(userRequestDto.getUsername());

        return ResponseEntity.ok()
                .header(JwtUtil.AUTHORIZATION_HEADER, token)
                .body(BaseResponse.of(LOGIN, ""));
    }

    // 로그인 사용자 이름 받기
    @GetMapping("/user-info")
    public String getUsername(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userDetails.getUser().getUsername();
    }

    //비밀번호 변경
    @PutMapping("/users/password")
    public ResponseEntity<BaseResponse<String>> updatePassword(@RequestBody @Valid PasswordRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        userService.updatePassword(requestDto, userDetails.getUser());
        return ResponseEntity.ok()
                .body(BaseResponse.of(UPDATE_PASSWORD, ""));
    }

    //회원 탈퇴
    @DeleteMapping("/users")
    public ResponseEntity<BaseResponse<String>> unregister(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        userService.unregister( userDetails.getUser());
        return ResponseEntity.ok()
                .body(BaseResponse.of(UNREGISTER_USER, ""));
    }
}
