package com.growith.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 존재하는 닉네임입니다."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "가입된 회원이 아닙니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 게시글을 찾을 수 없습니다."),
    USER_NOT_MATCH(HttpStatus.UNAUTHORIZED,"본인만 접근할 수 있습니다."),

    REQUEST_PARAM_NOT_MATCH(HttpStatus.BAD_REQUEST,"Request Parameter가 유효한지 확인해주세요."),

    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "로그인 후 이용해주세요!");
    private HttpStatus httpStatus;
    private String message;
}
