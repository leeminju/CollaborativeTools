package com.example.collaborativetools.user.service;

import com.example.collaborativetools.board.repository.BoardRepository;
import com.example.collaborativetools.comment.repository.CommentRepository;
import com.example.collaborativetools.global.constant.ErrorCode;
import com.example.collaborativetools.global.exception.ApiException;
import com.example.collaborativetools.global.jwt.JwtUtil;
import com.example.collaborativetools.global.jwt.UserDetailsImpl;
import com.example.collaborativetools.redis.dao.RedisDao;
import com.example.collaborativetools.redis.dto.TokenDto;
import com.example.collaborativetools.user.dto.LoginRequestDto;
import com.example.collaborativetools.user.dto.PasswordRequestDto;
import com.example.collaborativetools.user.dto.SignupRequestDto;
import com.example.collaborativetools.user.dto.UserInfoDto;
import com.example.collaborativetools.user.entitiy.User;
import com.example.collaborativetools.user.repository.UserRepository;
import com.example.collaborativetools.userboard.entity.UserBoard;
import com.example.collaborativetools.userboard.repository.UserBoardRepository;
import com.example.collaborativetools.usercard.repository.UserCardRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.example.collaborativetools.global.constant.ErrorCode.INVALID_TOKEN;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BoardRepository boardRepository;
    private final UserBoardRepository userBoardRepository;
    private final CommentRepository commentRepository;
    private final UserCardRepository userCardRepository;

    private final StringRedisTemplate stringRedisTemplate;
    private final JwtUtil jwtUtil;
    private final RedisDao redisDao;

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

    public TokenDto login(LoginRequestDto loginRequestDto) {
        String username = loginRequestDto.getUsername();
        String password = loginRequestDto.getPassword();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_USER));

        Authentication authentication = createAuthentication(password, user);
        setAuthentication(authentication);

        TokenDto tokenDto = jwtUtil.createToken(user.getUsername());
        Long expiration = jwtUtil.getExpiration(tokenDto.getRefreshToken());

        redisDao.setRefreshToken(username, tokenDto.getRefreshToken(), expiration);

        return tokenDto;
    }


    public void reissue(HttpServletRequest request, HttpServletResponse response) {
        String targetToken = jwtUtil.getRefreshTokenFromCookie(request);

        //토큰 검증
        if (!jwtUtil.validateToken(targetToken)) {
            throw new ApiException(INVALID_TOKEN);
        }

        //토큰에서 username 추출
        Claims claims = jwtUtil.getUserInfoFromToken(targetToken);
        String username = claims.getSubject();

        //db에서 refresh token 확인
        ValueOperations<String, String> stringValueOperations = stringRedisTemplate.opsForValue();
        String refreshToken = stringValueOperations.get(username);
        if (refreshToken == null) {
            throw new ApiException(INVALID_TOKEN);
        }

        //토큰 재발급
        TokenDto tokenDto = jwtUtil.createToken(username);
        jwtUtil.setTokenResponse(tokenDto, response);

        //재발급된 토큰 정보 저장
        stringValueOperations.getAndSet(username, tokenDto.getRefreshToken());
    }

    public void logout(HttpServletRequest request) {
        String accessToken = jwtUtil.getJwtFromHeader(request);

        if (!jwtUtil.validateToken(accessToken)) {
            throw new ApiException(INVALID_TOKEN);
        }

        Claims claims = jwtUtil.getUserInfoFromToken(accessToken);
        String username = claims.getSubject();
        Long expiration = jwtUtil.getExpiration(accessToken);//로그아웃 알아낸다.

        redisDao.setBlackList(accessToken, "logout", expiration);
        String refreshToken = redisDao.getRefreshToken(username);

        stringRedisTemplate.delete(username);
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

    @Transactional
    public void unregister(Long userId, User loginUser) {
        if (!userId.equals(loginUser.getId())) {
            throw new ApiException(ErrorCode.NOT_LOGIN_USER);
        }

        User user = userRepository.findById(userId).orElseThrow(
                () -> new ApiException(ErrorCode.NOT_FOUND_USER)
        );
        List<UserBoard> userBoardList = userBoardRepository.findByUserId(userId);
        userBoardRepository.deleteAllByUser(user);
        for (UserBoard userBoard : userBoardList) {
            if(userBoard.getRole().equals("ADMIN")){
                boardRepository.delete(userBoard.getBoard());
            }
        }


        userCardRepository.deleteAllByUser(user);
        commentRepository.deleteAllByUser(user);
        userRepository.delete(user);

    }

    public UserInfoDto getUserInfo(User loginUser) {
        User findUser = userRepository.findById(loginUser.getId()).orElseThrow(
                () -> new ApiException(ErrorCode.NOT_FOUND_USER)
        );

        return new UserInfoDto(findUser);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String password, User user) {
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ApiException(ErrorCode.NOT_EQUALS_PASSWORD);
        }

        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        return new UsernamePasswordAuthenticationToken(userDetails, user.getPassword(), userDetails.getAuthorities());
    }

    private void setAuthentication(Authentication authentication) {
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(authentication);
    }
}
