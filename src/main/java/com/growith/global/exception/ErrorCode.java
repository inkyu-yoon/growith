package com.growith.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 존재하는 닉네임입니다."),
    DUPLICATE_POST_LIKE(HttpStatus.CONFLICT, "좋아요는 중복 입력이 불가능합니다."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "가입된 회원이 아닙니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 상품 정보를 찾을 수 없습니다."),
    ALARM_NOT_FOUND(HttpStatus.NOT_FOUND, "알림 데이터를 찾을 수 없습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 게시글을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 댓글을 찾을 수 없습니다."),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 파일을 찾을 수 없습니다."),
    ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "회원 주소를 찾을 수 없습니다.(마이페이지에서 업데이트 해주세요.)"),
    USER_NOT_MATCH(HttpStatus.UNAUTHORIZED,"본인만 접근할 수 있습니다."),
    LIKE_NOT_ALLOWED(HttpStatus.UNAUTHORIZED,"본인 게시글에 좋아요를 누를 수 없습니다."),
    LACK_OF_QUANTITY(HttpStatus.BAD_REQUEST,"재고를 초과하여 주문할 수 없습니다."),
    LACK_OF_POINT(HttpStatus.BAD_REQUEST,"포인트가 부족합니다."),
    ALLOWED_ONLY_ADMIN(HttpStatus.UNAUTHORIZED,"관리자만 요청할 수 있습니다."),

    REQUEST_PARAM_NOT_MATCH(HttpStatus.BAD_REQUEST,"Request Parameter가 유효한지 확인해주세요."),

    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "로그인 후 이용해주세요!"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "다시 로그인 해주세요!"),
    WRONG_FILE_FORMAT(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일입니다."),
    EMPTY_FILE(HttpStatus.BAD_REQUEST, "파일이 존재하지 않습니다."),
    FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");

    private HttpStatus httpStatus;
    private String message;
}
