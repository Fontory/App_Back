package com.fontservice.fontory.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FindUserIdResponseDto {
    private Integer status;
    private String message;
    private String userId;  // 성공 시 반환, 실패 시 null
}
