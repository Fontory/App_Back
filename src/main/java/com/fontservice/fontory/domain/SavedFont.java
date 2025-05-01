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
@Table(name = "saved_fonts",
        uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "fontId"}))
public class SavedFont {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "saved_font_id")
    private Integer savedFontId;

    @Column(name = "user_id", nullable = false, length = 30)
    private String userId;

    @Column(name = "font_id", nullable = false)
    private Integer fontId;

    @Column(name = "saved_at", nullable = false)
    private LocalDateTime savedAt = LocalDateTime.now();
}
