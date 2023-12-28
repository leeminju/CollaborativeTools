package com.example.collaborativetools.global.constant;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /* 400 BAD_REQUEST : 잘못된 요청 */
    INVALID_VALUE(400, "유효하지 않은 값입니다."),
    INVALID_TOKEN(400, "유효하지 않은 토큰 입니다."),
    NOT_EQUALS_PASSWORD(400, "비밀번호가 일치하지 않습니다."),

    /* 401 UNAUTHORIZED  :  인증 되지 않음 */

    /* 403 FORBIDDEN  :  권한 없음 */

    /* 404 NOT_FOUND : Resource 권한이 없음 */

    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
    NOT_FOUND_USER(404, "존재하지 않는 사용자 입니다."),
    NOT_FOUND_BOARD(404, "존재하지 않는 게시판 입니다."),
    NOT_FOUND_COLUMN(404, "존재하지 않는 컬럼 입니다."),
    /* 409 CONFLICT : Resource 중복 */
    IS_DUPLICATE_USERNAME(409, "중복된 username입니다."),

    /* 500 INTERNAL_SERVER_ERROR : 서버 에러 */
    INTERNAL_SERVER_ERROR(500, "내부 서버 에러입니다.");


    private final int httpStatus;
    private final String message;
    }
