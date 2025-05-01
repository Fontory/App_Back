package com.fontservice.fontory.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "practice_sheets")
public class PracticeSheet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer sheetId;

    @Column(nullable = false, length = 30)
    private String userId;

    @Column(nullable = true)
    private Integer fontId;

    @Column(nullable = true)
    private Integer backgroundId;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String phrase;

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
