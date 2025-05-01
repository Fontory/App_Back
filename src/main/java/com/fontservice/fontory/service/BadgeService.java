package com.fontservice.fontory.service;

import com.fontservice.fontory.domain.Badge;
import com.fontservice.fontory.domain.User;
import com.fontservice.fontory.domain.UserBadge;
import com.fontservice.fontory.dto.mypage.BadgeResponseDto;
import com.fontservice.fontory.repository.BadgeRepository;
import com.fontservice.fontory.repository.PostRepository;
import com.fontservice.fontory.repository.UserBadgeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final PostRepository postRepository;

    public List<BadgeResponseDto> getAllBadges() {
        List<Badge> badges = badgeRepository.findAll();
        return badges.stream()
                .map(badge -> new BadgeResponseDto(
                        badge.getName(),
                        badge.getDescription(),
                        badge.getRequiredPostCount(),
                        badge.getIconUrl()
                ))
                .collect(Collectors.toList());
    }

    public List<BadgeResponseDto> getMyBadges(User user) {
        List<UserBadge> userBadges = userBadgeRepository.findByUser(user);
        return userBadges.stream()
                .map(userBadge -> {
                    Badge badge = userBadge.getBadge();
                    return new BadgeResponseDto(
                            badge.getName(),
                            badge.getDescription(),
                            badge.getRequiredPostCount(),
                            badge.getIconUrl()
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void checkAndAcquireBadge(User user) {
        int postCount = postRepository.countByUser(user);

        // 1. 이미 사용자가 가진 뱃지 목록 조회
        List<UserBadge> userBadges = userBadgeRepository.findByUser(user);
        Set<Integer> acquiredBadgeIds = userBadges.stream()
                .map(ub -> ub.getBadge().getBadgeId())
                .collect(Collectors.toSet());

        // 2. 모든 뱃지 목록 조회
        List<Badge> allBadges = badgeRepository.findAll();

        // 3. 조건을 만족하지만 아직 없는 뱃지가 있다면 획득
        for (Badge badge : allBadges) {
            if (postCount >= badge.getRequiredPostCount() && !acquiredBadgeIds.contains(badge.getBadgeId())) {
                UserBadge userBadge = UserBadge.builder()
                        .user(user)
                        .badge(badge)
                        .acquiredAt(LocalDateTime.now())
                        .build();
                userBadgeRepository.save(userBadge);
            }
        }
    }
}
