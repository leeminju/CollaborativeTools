package com.example.collaborativetools.global.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCode {

    /* 200 OK */
    GET_BOARD(200,"보드 조회 완료"),
    UPDATE_BOARD(200,"보드 수정 완료"),
    DELETE_BOARD(200,"보드 삭제 완료"),
    INVITE_BOARD(200,"보드 유저 초대 완료"),
    GET_MEMBER_BOARD(200,"보드 멤버 조회 완료"),

    /* 201 CREATED : Resource 생성 완료 */
    SIGNUP(201, "회원가입 성공"),
    LOGIN(201, "로그인 성공"),
    CREATED_BOARD(201, "게시판 생성 완료"),
    CREATED_COLUMNS(201, "컬럼 추가 완료"),
    CREATED_LIKE(201, "카드 추가 완료");

    private final int httpStatus;
    private final String message;
}
