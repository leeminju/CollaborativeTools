package com.example.collaborativetools.global.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /* 400 BAD_REQUEST : 잘못된 요청 */
    INVALID_VALUE(400, "유효하지 않은 값입니다."),
    INVALID_TOKEN(400, "유효하지 않은 토큰 입니다."),
    NOT_EQUALS_CONFIRM_PASSWORD(400, "비밀번호 확인이 일치하지 않습니다."),
    NOT_EQUALS_PASSWORD(400, "현재 비밀번호와 일치하지 않습니다."),
    EQUALS_CURRENT_PASSWORD(400, "새 비밀번호가 현재 비밀번호와 동일합니다."),
    NOT_LOGIN_USER(400, "찾는 유저가 로그인 유저와 다릅니다."),
  
    /* 401 UNAUTHORIZED  :  인증 되지 않음 */

    /* 403 FORBIDDEN  :  권한 없음 */
    NO_BOARD_AUTHORITY_EXCEPTION(403, "보드 권한 없습니다."),
    NO_COMMENT_AUTHORITY_EXCEPTION(403, "댓글 권한 없습니다."),
  
    /* 404 NOT_FOUND : Resource 권한이 없음 */
    NOT_FOUND_USER_EXCEPTION(404, "해당 유저는 없습니다."),
    NOT_FOUND_BOARD_EXCEPTION(404, "해당 보드는 없습니다."),
    NOT_FOUND_USER_BOARD_EXCEPTION(404, "해당 유저에 관한 보드는 없습니다."),
  
    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
    NOT_FOUND_USER(404, "존재하지 않는 사용자 입니다."),
    NOT_FOUND_BOARD(404, "존재하지 않는 게시판 입니다."),
    NOT_FOUND_COLUMN(404, "존재하지 않는 컬럼 입니다."),

    NOT_FOUND_CARD(404,"존재하지 않는 카드입니다."),
    NOT_FOUND_COMMENT(404,"존재하지 않는 댓글입니다."),



    /* 409 CONFLICT : Resource 중복 */
    IS_DUPLICATE_USERNAME(409, "중복된 username입니다."),
    USER_ALREADY_JOINED_BOARD(409,"이미 보드에 참여중인 유저 입니다."),

    /* 500 INTERNAL_SERVER_ERROR : 서버 에러 */
    INTERNAL_SERVER_ERROR(500, "내부 서버 에러입니다.");


    private final int httpStatus;
    private final String message;
}
