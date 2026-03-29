package com.swyp.server.domain.growth.service;

import com.swyp.server.domain.family.entity.FamilyRelation;
import com.swyp.server.domain.family.repository.FamilyRelationRepository;
import com.swyp.server.domain.growth.dto.ChildGrowthResponse;
import com.swyp.server.domain.growth.dto.ChildrenGrowthResponse;
import com.swyp.server.domain.growth.dto.GrowthHabitResponse;
import com.swyp.server.domain.growth.dto.GrowthTodoResponse;
import com.swyp.server.domain.habit.repository.HabitDailyCompletionRepository;
import com.swyp.server.domain.todo.repository.TodoRepository;
import com.swyp.server.domain.user.entity.User;
import com.swyp.server.global.util.DateUtils;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GrowthService {

    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter WEEK_RANGE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy/MM/dd (E)", Locale.KOREAN);

    private final TodoRepository todoRepository;
    private final HabitDailyCompletionRepository habitDailyCompletionRepository;
    private final FamilyRelationRepository familyRelationRepository;

    public GrowthTodoResponse getTodoGrowth(Long userId) {
        LocalDate today = LocalDate.now(SEOUL_ZONE);
        LocalDate startDate = DateUtils.getWeekStart(today);
        LocalDate endDate = DateUtils.getWeekEnd(today);

        int starCount = calculateTodoStarCount(userId, startDate, endDate);
        String weekRange = formatWeekRange(startDate, endDate);

        return new GrowthTodoResponse(starCount, weekRange);
    }

    public GrowthHabitResponse getHabitGrowth(Long userId) {
        LocalDate today = LocalDate.now(SEOUL_ZONE);
        LocalDate startDate = DateUtils.getWeekStart(today);
        LocalDate endDate = DateUtils.getWeekEnd(today);

        int completedDays =
                habitDailyCompletionRepository
                        .findAllByUserIdAndCompletionDateBetween(userId, startDate, endDate)
                        .size();
        int starCount = calculateHabitStarCount(completedDays);
        String weekRange = formatWeekRange(startDate, endDate);

        return new GrowthHabitResponse(starCount, weekRange);
    }

    public ChildrenGrowthResponse getChildrenGrowth(Long parentId) {
        LocalDate today = LocalDate.now(SEOUL_ZONE);
        LocalDate startDate = DateUtils.getWeekStart(today);
        LocalDate endDate = DateUtils.getWeekEnd(today);
        String weekRange = formatWeekRange(startDate, endDate);

        List<FamilyRelation> relations = familyRelationRepository.findAllByOwnerUserId(parentId);
        List<Long> childIds =
                relations.stream()
                        .filter(r -> r.getTargetUser() != null)
                        .map(r -> r.getTargetUser().getId())
                        .toList();

        Map<Long, Long> habitCompletionCountMap =
                childIds.isEmpty()
                        ? Map.of()
                        : habitDailyCompletionRepository
                                .countCompletionsByUserIds(childIds, startDate, endDate)
                                .stream()
                                .collect(
                                        Collectors.toMap(
                                                row -> ((Number) row[0]).longValue(),
                                                row -> ((Number) row[1]).longValue()));

        List<ChildGrowthResponse> children =
                relations.stream()
                        .filter(r -> r.getTargetUser() != null)
                        .map(
                                relation -> {
                                    User child = relation.getTargetUser();
                                    int todoStarCount =
                                            calculateTodoStarCount(
                                                    child.getId(), startDate, endDate);
                                    int habitCompletedDays =
                                            habitCompletionCountMap
                                                    .getOrDefault(child.getId(), 0L)
                                                    .intValue();
                                    int habitStarCount =
                                            calculateHabitStarCount(habitCompletedDays);
                                    return new ChildGrowthResponse(
                                            child.getId(),
                                            child.getNickname(),
                                            todoStarCount,
                                            habitStarCount,
                                            weekRange);
                                })
                        .toList();

        return new ChildrenGrowthResponse(children);
    }

    private int calculateTodoStarCount(Long userId, LocalDate startDate, LocalDate endDate) {
        int total = todoRepository.countAllByUserIdAndDateBetween(userId, startDate, endDate);
        if (total == 0) return 0;
        int completed =
                todoRepository.countCompletedByUserIdAndDateBetween(userId, startDate, endDate);
        double rate = (double) completed / total * 100;
        if (rate >= 80) return 3;
        if (rate >= 50) return 2;
        return 1;
    }

    private int calculateHabitStarCount(int completedDays) {
        if (completedDays >= 6) return 3;
        if (completedDays >= 3) return 2;
        if (completedDays >= 1) return 1;
        return 0;
    }

    private String formatWeekRange(LocalDate startDate, LocalDate endDate) {
        return startDate.format(WEEK_RANGE_FORMATTER)
                + " ~ "
                + endDate.format(WEEK_RANGE_FORMATTER);
    }
}
