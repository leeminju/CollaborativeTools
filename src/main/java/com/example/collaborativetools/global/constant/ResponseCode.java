package com.example.collaborativetools.global.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCode {

    /* 200 OK */
    GET_COLUMNS(200, "컬럼 조회 완료"),
    UPDATED_COLUMNS(200, "컬럼 수정 완료"),
    DELETED_COLUMNS(200, "컬럼 삭제 완료"),

    UPDATED_COMMENT(200, "댓글 수정 완료"),
    DELETED_COMMENT(200, "댓글 삭제 완료"),
    DELETED_CARD(200, "카드 삭제 완료"),

    GET_BOARD(200, "보드 조회 완료"),
    UPDATE_BOARD(200, "보드 수정 완료"),
    DELETE_BOARD(200, "보드 삭제 완료"),
    INVITE_BOARD(200, "보드 유저 초대 완료"),
    GET_MEMBER_BOARD(200, "보드 멤버 조회 완료"),

    LOGIN(200, "로그인 성공"),
    LOGOUT(200, "로그아웃 성공"),
    UPDATE_PASSWORD(200, "비밀번호 변경 완료"),
    UNREGISTER_USER(200, "회원 탈퇴 완료"),
    GET_USER_INFO(200, "회원 정보 조회 완료"),

    /* 201 CREATED : Resource 생성 완료 */
    SIGNUP(201, "회원가입 성공"),
    CREATED_BOARD(201, "게시판 생성 완료"),
    CREATED_COLUMNS(201, "컬럼 추가 완료"),
    CREATED_CARD(201, "카드 추가 완료"),
    REISSUE_TOKEN(201, "토큰 재발급 완료"),
    CREATED_COMMENT(201, "댓글 추가 완료");

    private final int httpStatus;
    private final String message;
}
