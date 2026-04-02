package com.swyp.server.domain.habit;

import com.swyp.server.domain.habit.entity.Habit;
import com.swyp.server.domain.habit.entity.HabitDuration;
import com.swyp.server.domain.habit.entity.RewardStatus;
import com.swyp.server.domain.habit.repository.HabitRepository;
import com.swyp.server.domain.user.entity.Role;
import com.swyp.server.domain.user.entity.User;
import com.swyp.server.domain.user.entity.UserType;
import com.swyp.server.domain.user.repository.UserRepository;
import com.swyp.server.global.config.JpaAuditingConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;

@DataJpaTest
@Import(JpaAuditingConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class HabitRepositoryTest {

    @Autowired private HabitRepository habitRepository;

    @Autowired private UserRepository userRepository;

    @PersistenceContext private EntityManager entityManager;

    @Test
    @DisplayName("매일 자정 보상 확인중, 진행중 상태의 습관들은 완료 여부가 초기화되어야 한다.")
    void resetDailyHabits() {
        User user =
                User.builder()
                        .email("testEmail")
                        .nickname("testNickname")
                        .profileImageUrl("testProfile")
                        .role(Role.USER)
                        .build();

        user.completeProfile("testNickname", UserType.CHILD, "testCode");
        user.agreeToTerms();

        userRepository.save(user);

        Habit habit1 =
                Habit.builder()
                        .user(user)
                        .title("testTitle")
                        .duration(HabitDuration.THREE_DAYS)
                        .reward("testReward")
                        .build();

        Habit habit2 =
                Habit.builder()
                        .user(user)
                        .title("testTitle")
                        .duration(HabitDuration.THREE_DAYS)
                        .reward("testReward")
                        .build();

        habit2.complete();
        habit2.updateRewardStatus(RewardStatus.COMPLETE);

        habitRepository.saveAll(List.of(habit1, habit2));

        habitRepository.resetAllHabits();
        entityManager.clear();

        Habit progressedHabit = habitRepository.findById(habit1.getId()).get();
        Habit completedHabit = habitRepository.findById(habit2.getId()).get();

        Assertions.assertThat(progressedHabit.isCompleted()).isFalse();
        Assertions.assertThat(completedHabit.isCompleted()).isTrue();
    }

    @Test
    @DisplayName("설정기간이 만료된 자녀의 습관은 보상 대기중 상태로 부모의 습관은 완료 상태로 변경되어야 한다.")
    void updateExpiredHabitsStatus() {
        User child =
                User.builder()
                        .email("testEmail1")
                        .nickname("testNickname1")
                        .profileImageUrl("testProfile1")
                        .role(Role.USER)
                        .build();

        User parent =
                User.builder()
                        .email("testEmail2")
                        .nickname("testNickname2")
                        .profileImageUrl("testProfile2")
                        .role(Role.USER)
                        .build();

        child.completeProfile("testNickname1", UserType.CHILD, "testCo");
        child.agreeToTerms();

        parent.completeProfile("testNickname2", UserType.PARENT, "testde");
        parent.agreeToTerms();

        userRepository.saveAll(List.of(child, parent));

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        LocalDateTime past = now.minusDays(1); // 이미 만료된 시간

        Habit childHabit =
                Habit.builder()
                        .user(child)
                        .title("testTitle")
                        .duration(HabitDuration.THREE_DAYS)
                        .reward("testReward")
                        .build();

        Habit parentHabit =
                Habit.builder()
                        .user(parent)
                        .title("testTitle")
                        .duration(HabitDuration.THREE_DAYS)
                        .reward(null)
                        .build();

        childHabit.complete();
        childHabit.updateRewardStatus(RewardStatus.IN_PROGRESS);
        ReflectionTestUtils.setField(childHabit, "expiredAt", past);

        parentHabit.complete();
        parentHabit.updateRewardStatus(RewardStatus.IN_PROGRESS);
        ReflectionTestUtils.setField(parentHabit, "expiredAt", past);

        habitRepository.saveAll(List.of(childHabit, parentHabit));

        habitRepository.updateExpiredHabitsStatus(now);
        entityManager.clear();

        Habit updateChild = habitRepository.findById(childHabit.getId()).get();
        Habit updateParent = habitRepository.findById(parentHabit.getId()).get();

        Assertions.assertThat(updateChild.getStatus()).isEqualTo(RewardStatus.REWARD_WAITING);
        Assertions.assertThat(updateParent.getStatus()).isEqualTo(RewardStatus.COMPLETE);
    }
}
