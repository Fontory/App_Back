package com.fontservice.fontory.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.fontservice.fontory.domain.User; //유저 프로필 join에 쓰임

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "fonts")
public class Font {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "font_id")
    private Integer fontId;

    //유저프로필 join
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "otf_url", nullable = false, length = 255)
    private String otfUrl;

    @Column(name = "ttf_url", nullable = false, length = 255)
    private String ttfUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "original_image_url", nullable = false, length = 255)
    private String originalImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_public", nullable = false, length = 1)
    private PublicStatus isPublic;

    @Column(name = "creator_rating")
    private Byte creatorRating;

    @Column(name = "vector_similarity")
    private Float vectorSimilarity;

    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;

    @Column(name = "download_count", nullable = false)
    private Integer downloadCount = 0;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum PublicStatus {
        Y, N
    }
}