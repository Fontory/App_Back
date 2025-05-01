package com.fontservice.fontory.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "font_likes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "fontId"}))
public class FontLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "font_like_id")
    private Integer fontLikeId;

    @Column(name = "user_id", nullable = false, length = 30)
    private String userId;

    @Column(name = "font_id", nullable = false)
    private Integer fontId;

    @Column(name = "liked_at", nullable = false)
    private LocalDateTime likedAt = LocalDateTime.now();
}