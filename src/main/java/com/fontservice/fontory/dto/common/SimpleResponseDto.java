package com.fontservice.fontory.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SimpleResponseDto<T> {
    private Integer status;
    private String message;
    private T data;

    // 성공 응답 생성
    public static <T> SimpleResponseDto<T> success(String message, T data) {
        return new SimpleResponseDto<>(200, message, data);
    }

    // 실패 응답 생성 (data 없이)
    public static <T> SimpleResponseDto<T> fail(String message) {
        return new SimpleResponseDto<>(500, message, null);
    }
}
