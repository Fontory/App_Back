package com.fontservice.fontory.repository;

import com.fontservice.fontory.domain.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BadgeRepository extends JpaRepository<Badge, Integer> {
    List<Badge> findAll();
}
