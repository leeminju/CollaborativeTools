package com.example.collaborativetools.global.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCode {

    /* 200 OK */


    /* 201 CREATED : Resource 생성 완료 */
    SIGNUP(201, "회원가입 성공"),
    LOGIN(201, "로그인 성공"),
    CREATED_BOARD(201, "게시판 생성 완료"),
    CREATED_COLUMNS(201, "컬럼 추가 완료"),
    CREATED_LIKE(201, "카드 추가 완료"),
    DELETED_CARD(200,"카드 삭제 완료");

    private final int httpStatus;
    private final String message;
}
