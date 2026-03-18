package com.swyp.server.domain.todo.entity;

import com.swyp.server.domain.user.entity.UserType;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TodoCategory {
    STUDY("공부", Set.of(UserType.CHILD)),
    HOMEWORK("숙제", Set.of(UserType.CHILD)),
    EXERCISE("운동", Set.of(UserType.CHILD, UserType.PARENT)),
    CLEANING("정리", Set.of(UserType.CHILD, UserType.PARENT)),
    READING("독서", Set.of(UserType.CHILD, UserType.PARENT)),
    HOUSEWORK("집안일", Set.of(UserType.CHILD, UserType.PARENT)),
    CREATIVE_ACTIVITY("창의활동", Set.of(UserType.CHILD)),
    WORK("업무", Set.of(UserType.PARENT)),
    APPOINTMENT("약속", Set.of(UserType.PARENT)),
    PARENTING("육아", Set.of(UserType.PARENT));

    private final String label;
    private final Set<UserType> allowedUserTypes;

    public boolean isAllowed(UserType userType) {
        if (userType == null) {
            return false;
        }
        return allowedUserTypes.contains(userType);
    }
}
