package com.fontservice.fontory.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nonapi.io.github.classgraph.json.Id;

@Entity
@Table(name = "BADGES")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Badge {

    @Id
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer badgeId;

    private String name;

    private String description;

    private int requiredPostCount;

    private String iconUrl;

}
