package com.swyp.server.domain.family.service;

import com.swyp.server.domain.family.dto.FamilyDashBoardResponse;
import com.swyp.server.domain.family.dto.FamilyMemberDashboardResponse;
import com.swyp.server.domain.family.dto.HabitSummary;
import com.swyp.server.domain.family.dto.TodoSummary;
import com.swyp.server.domain.family.entity.FamilyRelation;
import com.swyp.server.domain.family.repository.FamilyRelationRepository;
import com.swyp.server.domain.habit.repository.HabitRepository;
import com.swyp.server.domain.todo.entity.Todo;
import com.swyp.server.domain.todo.repository.TodoRepository;
import com.swyp.server.domain.user.entity.User;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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

        List<Todo> todos = todoRepository.findAllByUserIdInAndTodoDate(membersIds, today);

        Map<Long, List<Todo>> todoMap =
                todos.stream().collect(Collectors.groupingBy(todo -> todo.getUser().getId()));

        Set<Long> userIdsWithIncompleteHabit =
                Set.copyOf(habitRepository.findUserIdsWithIncompleteHabit(membersIds));

        Set<Long> userIdsWithNoHabit =
                Set.copyOf(habitRepository.findUserIdsWithNoHabit(membersIds));

        List<FamilyMemberDashboardResponse> memberDashboardResponseList =
                members.stream()
                        .map(
                                member -> {
                                    List<Todo> memberTodos =
                                            todoMap.getOrDefault(member.getId(), List.of());

                                    int totalCount = memberTodos.size();
                                    int completedCount =
                                            (int)
                                                    memberTodos.stream()
                                                            .filter(Todo::isCompleted)
                                                            .count();

                                    boolean habitCompleted =
                                            !userIdsWithIncompleteHabit.contains(member.getId())
                                                    && !userIdsWithNoHabit.contains(member.getId());
                                    return new FamilyMemberDashboardResponse(
                                            member.getId(),
                                            member.getNickname(),
                                            new TodoSummary(totalCount, completedCount),
                                            new HabitSummary(habitCompleted));
                                })
                        .toList();

        return new FamilyDashBoardResponse(memberDashboardResponseList);
    }
}
