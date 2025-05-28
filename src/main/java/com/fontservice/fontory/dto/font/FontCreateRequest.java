package com.fontservice.fontory.dto.font;

import lombok.Data;

@Data
public class FontCreateRequest {
    private String fontName;
    private String originalImageUrl;
    private String ttfUrl;
    private String otfUrl;
}
