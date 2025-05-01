package com.fontservice.fontory.repository;

import com.fontservice.fontory.domain.User;
import com.fontservice.fontory.domain.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Integer> {
    List<UserBadge> findByUser(User user);
}
