package com.fontservice.fontory.dto.sheet;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BackgroundResponse {
    private Integer backgroundId;
    private String name;
    private String imageUrl;
}
