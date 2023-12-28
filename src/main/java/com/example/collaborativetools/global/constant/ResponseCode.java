package com.example.collaborativetools.global.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCode {

    /* 200 OK */
    LOGIN(200, "로그인 성공"),
    UPDATE_PASSWORD(200, "비밀번호 변경 완료"),
    UNREGISTER_USER(200, "회원 탈퇴 완료"),
    GET_USER_INFO(200, "회원 정보 조회 완료"),

    /* 201 CREATED : Resource 생성 완료 */
    SIGNUP(201, "회원가입 성공"),
    CREATED_BOARD(201, "게시판 생성 완료"),
    CREATED_COLUMNS(201, "컬럼 추가 완료"),
    CREATED_LIKE(201, "카드 추가 완료");

    private final int httpStatus;
    private final String message;
}
