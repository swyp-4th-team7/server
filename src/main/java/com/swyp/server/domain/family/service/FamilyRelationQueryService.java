package com.swyp.server.domain.family.service;

import com.swyp.server.domain.family.dto.FamilyDashBoardResponse;
import com.swyp.server.domain.family.dto.FamilyMemberDashboardResponse;
import com.swyp.server.domain.family.entity.FamilyRelation;
import com.swyp.server.domain.family.repository.FamilyRelationRepository;
import com.swyp.server.domain.habit.repository.HabitRepository;
import com.swyp.server.domain.todo.repository.TodoRepository;
import com.swyp.server.domain.user.entity.User;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FamilyRelationQueryService {
    private final FamilyRelationRepository familyRelationRepository;
    private final TodoRepository todoRepository;
    private final HabitRepository habitRepository;

    public FamilyDashBoardResponse getDashBoard(Long userId) {
        List<FamilyRelation> relations = familyRelationRepository.findAllByOwnerUserId(userId);

        List<User> members = relations.stream().map(FamilyRelation::getTargetUser).toList();

        if (members.isEmpty()) {
            return new FamilyDashBoardResponse(List.of());
        }

        List<Long> membersIds = members.stream().map(User::getId).toList();

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        Set<Long> userIdsWithIncompleteTodo =
                Set.copyOf(todoRepository.findUserIdsWithIncompleteTodo(membersIds, today));

        Set<Long> userIdsWithIncompleteHabit =
                Set.copyOf(habitRepository.findUserIdsWithIncompleteHabit(membersIds));

        Set<Long> userIdsWithNoHabit =
                Set.copyOf(habitRepository.findUserIdsWithNoHabit(membersIds));

        List<FamilyMemberDashboardResponse> memberDashboardResponseList =
                members.stream()
                        .map(
                                member -> {
                                    boolean todoCompleted =
                                            !userIdsWithIncompleteTodo.contains(member.getId());

                                    boolean habitCompleted =
                                            !userIdsWithIncompleteHabit.contains(member.getId())
                                                    && !userIdsWithNoHabit.contains(member.getId());
                                    return new FamilyMemberDashboardResponse(
                                            member.getId(),
                                            member.getNickname(),
                                            todoCompleted,
                                            habitCompleted);
                                })
                        .toList();

        return new FamilyDashBoardResponse(memberDashboardResponseList);
    }
}
