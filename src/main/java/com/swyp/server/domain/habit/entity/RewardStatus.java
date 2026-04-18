package com.swyp.server.domain.habit.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RewardStatus {
    REWARD_CHECKING("보상 확인중"),
    REWARD_WAITING("보상 대기중"),
    IN_PROGRESS("진행중"),
    COMPLETE("완료"),
    ALL("전체"),
    FAIL("실패");

    private final String label;

    public boolean isAll() {
        return this == ALL;
    }
}
