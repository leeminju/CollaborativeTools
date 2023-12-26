package com.example.collaborativetools.global.dto;


import com.example.collaborativetools.global.constant.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class BaseResponse<T> {
    private final String msg;
    private final Integer statusCode;
    private final T data;

    public static <T> BaseResponse<T> of(String msg, Integer statusCode, T data) {
        return new BaseResponse<>(msg, statusCode, data);
    }

    public static <T> BaseResponse<T> of(ResponseCode responseCode, T data) {
        return new BaseResponse<>(
                responseCode.getMessage(),
                responseCode.getHttpStatus(),
                data
        );
    }
}
