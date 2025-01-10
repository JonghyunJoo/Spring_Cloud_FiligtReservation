package com.example.seatservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    UNAVAILABLE_SEAT("UNAVAILABLE_SEAT", "이용 불가능한 좌석입니다.", HttpStatus.BAD_REQUEST),
    NOT_FOUND_SEAT("NOT_FOUND_SEAT", "좌석을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    NOT_FOUND_MEMBER("NOT_FOUND_MEMBER", "회원을 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    NOT_FOUND_FLIGHT("NOT_FOUND_FLIGHT", "비행기를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    ALREADY_RESERVED_SEAT("ALREADY_RESERVED_SEAT", "이미 예약된 좌석입니다", HttpStatus.CONFLICT),
    INVALID_REQUEST("INVALID_REQUEST","요청이 처리 불가능합니다" , HttpStatus.BAD_REQUEST);

    private final String key;
    private final String message;
    private final HttpStatus status;


    ErrorCode(String key, String message, HttpStatus status) {
        this.key = key;
        this.message = message;
        this.status = status;
    }
}
