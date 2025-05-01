package com.fontservice.fontory.dto.font;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyFontResponse {
    private Integer fontId;
    private String name;
    private String type; // "created" 또는 "saved"
}
