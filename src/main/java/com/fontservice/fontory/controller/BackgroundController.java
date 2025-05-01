package com.fontservice.fontory.controller;

import com.fontservice.fontory.domain.Background;
import com.fontservice.fontory.dto.sheet.BackgroundResponse;
import com.fontservice.fontory.repository.BackgroundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/backgrounds")
@RequiredArgsConstructor
public class BackgroundController {

    private final BackgroundRepository backgroundRepository;

    @GetMapping
    public List<BackgroundResponse> getBackgrounds() {
        List<Background> backgrounds = backgroundRepository.findAll();
        return backgrounds.stream()
                .map(bg -> BackgroundResponse.builder()
                        .backgroundId(bg.getBackgroundId())
                        .name(bg.getName())
                        .imageUrl("/backgrounds/" + bg.getImageUrl())
                        .build())
                .collect(Collectors.toList());
    }
}
