package com.example.collaborativetools.global.aop;

import com.example.collaborativetools.global.dto.BaseResponse;
import com.example.collaborativetools.global.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;

import static com.example.collaborativetools.global.constant.ErrorCode.*;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * [Exception] RuntimeException 반환하는 경우
     *
     * @param ex RuntimeException
     * @return ResponseEntity<BaseResponse>
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<BaseResponse<Void>> runtimeExceptionHandler(RuntimeException ex) {
        log.error("Runtime Exceptions :", ex);
        return ResponseEntity.internalServerError()
                .body(
                        BaseResponse.of(
                                INTERNAL_SERVER_ERROR.getMessage(),
                                INTERNAL_SERVER_ERROR.getHttpStatus(),
                                null
                        )
                );
    }

    /**
     * [Exception] API 요청 시 '객체' 혹은 '파라미터' 데이터 값이 유효하지 않은 경우
     *
     * @param ex MethodArgumentNotValidException,
     * @return ResponseEntity<BaseResponse>
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.error("handleMethodArgumentNotValidException", ex);
        BindingResult bindingResult = ex.getBindingResult();
        HashMap<String, String> errors = new HashMap<>();
        bindingResult.getAllErrors()
                .forEach(error -> errors.put(((FieldError) error).getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest()
                .body(
                        BaseResponse.of(
                                INVALID_VALUE.getMessage(),
                                INVALID_VALUE.getHttpStatus(),
                                errors
                        )
                );
    }

    /**
     * [Exception] API 요청에 맞는 파라미터를 받지 못한 경우
     *
     * @param ex MissingServletRequestParameterException,
     * @return ResponseEntity<BaseResponse>
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                          HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                BaseResponse.of(
                        "누락된 파라미터 : " + ex.getParameterName(),
                        INVALID_VALUE.getHttpStatus(),
                        null
                )
        );
    }

    /**
     * [Exception] ApiException 반환하는 경우
     * ErrorCode 를 사용하여 공통예외를 반환하는 경우 핸들링
     *
     * @param ex ApiException
     * @return ResponseEntity<BaseResponse>
     */

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<BaseResponse<Void>> apiExceptionHandler(ApiException ex) {
        log.error("Runtime Exceptions :", ex);
        return ResponseEntity.status(ex.getErrorCode().getHttpStatus())
                .body(
                        BaseResponse.of(
                                ex.getErrorCode().getMessage(),
                                ex.getErrorCode().getHttpStatus(),
                                null
                        )
                );
    }

}
