package com.swyp.server.domain.schedule.service;

import com.swyp.server.domain.schedule.dto.ScheduleCreateRequest;
import com.swyp.server.domain.schedule.dto.ScheduleResponse;
import com.swyp.server.domain.schedule.dto.ScheduleUpdateRequest;
import com.swyp.server.domain.schedule.entity.Schedule;
import com.swyp.server.domain.schedule.repository.ScheduleRepository;
import com.swyp.server.domain.user.entity.User;
import com.swyp.server.domain.user.repository.UserRepository;
import com.swyp.server.global.exception.CustomException;
import com.swyp.server.global.exception.ErrorCode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private static final int SCHEDULE_LOOKUP_DAYS = 30;

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    @Transactional
    public Schedule createSchedule(Long userId, ScheduleCreateRequest request) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Schedule schedule =
                Schedule.builder()
                        .user(user)
                        .title(request.title())
                        .category(request.category())
                        .scheduleDate(request.scheduleDate())
                        .build();
        return scheduleRepository.save(schedule);
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponse> getSchedules(Long userId) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate end = today.plusDays(SCHEDULE_LOOKUP_DAYS);
        return scheduleRepository
                .findAllByUserIdAndScheduleDateBetweenOrderByScheduleDateAsc(userId, today, end)
                .stream()
                .map(ScheduleResponse::from)
                .toList();
    }

    @Transactional
    public void updateSchedule(Long userId, Long scheduleId, ScheduleUpdateRequest request) {
        Schedule schedule =
                scheduleRepository
                        .findByIdAndUserId(scheduleId, userId)
                        .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_NOT_FOUND));
        if (request.title() != null) {
            schedule.updateTitle(request.title());
        }
        if (request.category() != null) {
            schedule.updateCategory(request.category());
        }
        if (request.scheduleDate() != null) {
            schedule.updateScheduleDate(request.scheduleDate());
        }
    }

    @Transactional
    public void deleteSchedule(Long userId, Long scheduleId) {
        Schedule schedule =
                scheduleRepository
                        .findByIdAndUserId(scheduleId, userId)
                        .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_NOT_FOUND));
        schedule.delete();
    }
}
