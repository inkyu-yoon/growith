package com.growith.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "가입된 회원이 아닙니다."),
    USER_NOT_MATCH(HttpStatus.UNAUTHORIZED,"본인만 접근할 수 있습니다.");

    private HttpStatus httpStatus;
    private String message;
}
