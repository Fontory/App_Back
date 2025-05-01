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
@Table(name = "backgrounds")
public class Background {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer backgroundId;

    private String name;

    private String imageUrl;

    private LocalDateTime createdAt;
}
