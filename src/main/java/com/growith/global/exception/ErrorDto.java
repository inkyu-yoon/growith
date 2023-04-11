package com.growith.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorDto {
    private String errorCode;
    private String message;

    public ErrorDto(ErrorCode errorCode) {
        this.errorCode = errorCode.getHttpStatus().toString();
        this.message = errorCode.getMessage();
    }
}
