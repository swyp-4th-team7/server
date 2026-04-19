package com.swyp.server.domain.habit;

import com.swyp.server.domain.habit.dto.HabitRewardListResponse;
import com.swyp.server.domain.habit.entity.Habit;
import com.swyp.server.domain.habit.entity.HabitDuration;
import com.swyp.server.domain.habit.entity.RewardStatus;
import com.swyp.server.domain.habit.repository.HabitRepository;
import com.swyp.server.domain.habit.service.HabitService;
import com.swyp.server.domain.user.entity.Role;
import com.swyp.server.domain.user.entity.User;
import com.swyp.server.domain.user.entity.UserType;
import com.swyp.server.domain.user.repository.UserRepository;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class HabitRepositoryTest {

    @Autowired private HabitService habitService;

    @Autowired private HabitRepository habitRepository;

    @Autowired private UserRepository userRepository;

    @Autowired private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("status가 'FAIL'인 습관은 보상 조회 시 조회되지 않아야 한다.")
    void getHabitRewards() {

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

        Habit habitInProgress =
                Habit.builder()
                        .user(user)
                        .title("testTitle")
                        .duration(HabitDuration.THREE_DAYS)
                        .reward("testReward")
                        .build();

        habitInProgress.updateRewardStatus(RewardStatus.IN_PROGRESS);

        Habit habitRewardWaiting =
                Habit.builder()
                        .user(user)
                        .title("testTitle")
                        .duration(HabitDuration.THREE_DAYS)
                        .reward("testReward")
                        .build();

        habitRewardWaiting.updateRewardStatus(RewardStatus.REWARD_WAITING);

        Habit habitCompleted =
                Habit.builder()
                        .user(user)
                        .title("testTitle")
                        .duration(HabitDuration.THREE_DAYS)
                        .reward("testReward")
                        .build();

        habitCompleted.updateRewardStatus(RewardStatus.COMPLETE);

        Habit habitFailed =
                Habit.builder()
                        .user(user)
                        .title("testTitle")
                        .duration(HabitDuration.THREE_DAYS)
                        .reward("testReward")
                        .build();

        habitFailed.updateRewardStatus(RewardStatus.FAIL);

        habitRepository.saveAll(
                List.of(habitInProgress, habitRewardWaiting, habitCompleted, habitFailed));

        HabitRewardListResponse habitRewardsStatusAll =
                habitService.getHabitRewards(user.getId(), RewardStatus.ALL);
        HabitRewardListResponse habitRewardsFail =
                habitService.getHabitRewards(user.getId(), RewardStatus.FAIL);

        Assertions.assertThat(habitRewardsStatusAll.habitRewards())
                .hasSize(3)
                .extracting("status")
                .doesNotContain(RewardStatus.FAIL)
                .containsExactlyInAnyOrder(
                        RewardStatus.IN_PROGRESS,
                        RewardStatus.REWARD_WAITING,
                        RewardStatus.COMPLETE);

        Assertions.assertThat(habitRewardsFail.habitRewards()).isEmpty();
    }

    @Test
    @DisplayName("매일 자정 보상 확인중, 진행중 상태이며 수행 기간이 '3일', '7일'외의 습관들은 당일 미실행 시 실패 횟수가 1 증가하여야 한다.")
    void updateHabitFailCount() {

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

        // 진행중, 수행 기간 14일, 미완료 습관
        Habit fourteenDaysNotCompletedHabit =
                Habit.builder()
                        .user(user)
                        .title("testTitle")
                        .duration(HabitDuration.FOURTEEN_DAYS)
                        .reward("testReward")
                        .build();

        fourteenDaysNotCompletedHabit.updateRewardStatus(RewardStatus.IN_PROGRESS);

        // 진행중, 수행 기간 14일, 완료 습관
        Habit fourteenDaysCompleteHabit =
                Habit.builder()
                        .user(user)
                        .title("testTitle")
                        .duration(HabitDuration.FOURTEEN_DAYS)
                        .reward("testReward")
                        .build();

        fourteenDaysCompleteHabit.complete();
        fourteenDaysCompleteHabit.updateRewardStatus(RewardStatus.IN_PROGRESS);

        // 진행중, 수행 기간 7일, 미완료 습관
        Habit sevenDaysNotCompletedHabit =
                Habit.builder()
                        .user(user)
                        .title("testTitle")
                        .duration(HabitDuration.SEVEN_DAYS)
                        .reward("testReward")
                        .build();

        sevenDaysNotCompletedHabit.updateRewardStatus(RewardStatus.IN_PROGRESS);

        habitRepository.saveAll(
                List.of(
                        fourteenDaysNotCompletedHabit,
                        fourteenDaysCompleteHabit,
                        sevenDaysNotCompletedHabit));

        habitRepository.updateHabitFailCount();

        Habit fourteenDaysNotCompletedHabitInDB =
                habitRepository.findById(fourteenDaysNotCompletedHabit.getId()).get();
        Habit fourteenDaysCompletedHabitInDB =
                habitRepository.findById(fourteenDaysCompleteHabit.getId()).get();
        Habit sevenDaysNotCompletedHabitInDB =
                habitRepository.findById(sevenDaysNotCompletedHabit.getId()).get();

        Assertions.assertThat(fourteenDaysNotCompletedHabitInDB.getFailCount()).isEqualTo(1);
        Assertions.assertThat(fourteenDaysCompletedHabitInDB.getFailCount()).isEqualTo(0);
        Assertions.assertThat(sevenDaysNotCompletedHabitInDB.getFailCount()).isEqualTo(0);
    }

    @Test
    @DisplayName(
            "매일 자정 보상 확인중, 진행중 상태이며 수행 기간이 '3일', '7일'인 습관들은 당일 미실행 시 실패 상태가 되어야 하며 일일 성공 여부가 false가 되어야 한다.")
    void updateImmediateFailureHabits() {
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

        Habit threeDaysNotCompletedHabit =
                Habit.builder()
                        .user(user)
                        .title("testTitle")
                        .duration(HabitDuration.THREE_DAYS)
                        .reward("testReward")
                        .build();

        threeDaysNotCompletedHabit.updateRewardStatus(RewardStatus.IN_PROGRESS);

        Habit sevenDaysNotCompletedHabit =
                Habit.builder()
                        .user(user)
                        .title("testTitle")
                        .duration(HabitDuration.SEVEN_DAYS)
                        .reward("testReward")
                        .build();

        sevenDaysNotCompletedHabit.updateRewardStatus(RewardStatus.IN_PROGRESS);

        habitRepository.saveAll(List.of(threeDaysNotCompletedHabit, sevenDaysNotCompletedHabit));

        habitRepository.updateImmediateFailureHabits();

        Habit threeDaysNotCompletedHabitInDB =
                habitRepository.findById(threeDaysNotCompletedHabit.getId()).get();
        Habit sevenDaysNotCompletedHabitInDB =
                habitRepository.findById(sevenDaysNotCompletedHabit.getId()).get();

        Assertions.assertThat(threeDaysNotCompletedHabitInDB.getStatus())
                .isEqualTo(RewardStatus.FAIL);
        Assertions.assertThat(threeDaysNotCompletedHabitInDB.isCompleted()).isEqualTo(false);

        Assertions.assertThat(sevenDaysNotCompletedHabitInDB.getStatus())
                .isEqualTo(RewardStatus.FAIL);
        Assertions.assertThat(sevenDaysNotCompletedHabitInDB.isCompleted()).isEqualTo(false);
    }

    @Test
    @DisplayName(
            "매일 자정 보상 확인중, 진행중 상태이며 수행 기간이 '3일', '7일' 외 습관들은 실패 횟수가 2회 이상이면 실패 상태가 되어야 하며 일일 성공 여부가 false가 되어야 한다.")
    void updateCumulativeFailureHabits() {
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

        Habit fourteenDaysOneFailedHabit =
                Habit.builder()
                        .user(user)
                        .title("testTitle")
                        .duration(HabitDuration.FOURTEEN_DAYS)
                        .reward("testReward")
                        .build();

        fourteenDaysOneFailedHabit.updateRewardStatus(RewardStatus.IN_PROGRESS);

        Habit fourteenDaysTwoFailedHabit =
                Habit.builder()
                        .user(user)
                        .title("testTitle")
                        .duration(HabitDuration.FOURTEEN_DAYS)
                        .reward("testReward")
                        .build();

        fourteenDaysTwoFailedHabit.updateRewardStatus(RewardStatus.IN_PROGRESS);

        ReflectionTestUtils.setField(fourteenDaysOneFailedHabit, "failCount", 1);
        ReflectionTestUtils.setField(fourteenDaysTwoFailedHabit, "failCount", 2);
        habitRepository.saveAll(List.of(fourteenDaysOneFailedHabit, fourteenDaysTwoFailedHabit));

        habitRepository.updateCumulativeFailureHabits();

        Habit fourteenDaysOneFailedHabitInDB =
                habitRepository.findById(fourteenDaysOneFailedHabit.getId()).get();
        Habit fourteenDaysTwoFailedHabitInDB =
                habitRepository.findById(fourteenDaysTwoFailedHabit.getId()).get();

        Assertions.assertThat(fourteenDaysOneFailedHabitInDB.getStatus())
                .isNotEqualTo(RewardStatus.FAIL);

        Assertions.assertThat(fourteenDaysTwoFailedHabitInDB.getStatus())
                .isEqualTo(RewardStatus.FAIL);
        Assertions.assertThat(fourteenDaysTwoFailedHabitInDB.isCompleted()).isEqualTo(false);
    }

    @Test
    @DisplayName(
            "매일 자정 보상 확인중, 진행중 상태이며 수행 기간이 '3일', '7일' 외이며 실패 횟수가 2회 미만인 습관은 습관 생성 날짜를 기준으로 10일에 한번씩 습관 실패 횟수가 초기화된다.")
    void resetFailCount() {
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

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        LocalDateTime nowMinusTenDays = now.minusDays(10);

        Habit fourteenDaysOneFailedHabit =
                Habit.builder()
                        .user(user)
                        .title("testTitle")
                        .duration(HabitDuration.FOURTEEN_DAYS)
                        .reward("testReward")
                        .build();

        fourteenDaysOneFailedHabit.updateRewardStatus(RewardStatus.IN_PROGRESS);

        ReflectionTestUtils.setField(fourteenDaysOneFailedHabit, "failCount", 1);

        habitRepository.save(fourteenDaysOneFailedHabit);

        jdbcTemplate.update(
                "UPDATE habits SET created_at = ? WHERE id = ?",
                Timestamp.valueOf(nowMinusTenDays),
                fourteenDaysOneFailedHabit.getId());

        habitRepository.resetFailCount(now);

        Habit fourteenDaysOneFailedHabitInDB =
                habitRepository.findById(fourteenDaysOneFailedHabit.getId()).get();

        Assertions.assertThat(fourteenDaysOneFailedHabitInDB.getFailCount()).isEqualTo(0);
    }

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

        habit1.complete();
        habit1.updateRewardStatus(RewardStatus.IN_PROGRESS);

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

        Habit updateChild = habitRepository.findById(childHabit.getId()).get();
        Habit updateParent = habitRepository.findById(parentHabit.getId()).get();

        Assertions.assertThat(updateChild.getStatus()).isEqualTo(RewardStatus.REWARD_WAITING);
        Assertions.assertThat(updateParent.getStatus()).isEqualTo(RewardStatus.COMPLETE);
    }
}
