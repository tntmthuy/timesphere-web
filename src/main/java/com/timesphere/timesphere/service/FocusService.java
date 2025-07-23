package com.timesphere.timesphere.service;

import com.timesphere.timesphere.dto.focus.FocusSessionResponse;
import com.timesphere.timesphere.entity.FocusSession;
import com.timesphere.timesphere.entity.User;
import com.timesphere.timesphere.exception.AppException;
import com.timesphere.timesphere.exception.ErrorCode;
import com.timesphere.timesphere.mapper.FocusMapper;
import com.timesphere.timesphere.repository.FocusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FocusService {

    private final FocusRepository focusRepository;
    private final FocusMapper focusMapper;

    /**
     * Khởi tạo phiên tập trung mới
     */
    public FocusSessionResponse startSession(User user, String mode, int targetMinutes, String description) {
        if (targetMinutes <= 0 || targetMinutes > 240) {
            throw new AppException(ErrorCode.INVALID_KEY, "Thời lượng phải từ 1 đến 240 phút.");
        }

        if (!mode.equalsIgnoreCase("focus") && !mode.equalsIgnoreCase("break")) {
            throw new AppException(ErrorCode.INVALID_KEY, "Mode phải là 'focus' hoặc 'break'.");
        }

        FocusSession session = FocusSession.builder()
                .user(user)
                .mode(mode.toLowerCase())
                .targetMinutes(targetMinutes)
                .description(description)
                .status(FocusSession.Status.CANCELLED)
                .startedAt(LocalDateTime.now())
                .build();

        return focusMapper.toResponse(focusRepository.save(session));
    }

    /**
     * Kết thúc phiên, cập nhật trạng thái
     */
    public FocusSessionResponse endSession(Long sessionId, int actualMinutes) {
        if (actualMinutes < 0 || actualMinutes > 720) {
            throw new AppException(ErrorCode.INVALID_KEY, "Số phút thực tế không hợp lệ.");
        }

        FocusSession session = focusRepository.findById(sessionId)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_KEY, "Không tìm thấy session."));

        session.setActualMinutes(actualMinutes);
        session.setEndedAt(LocalDateTime.now());

        boolean isFocus = session.getMode().equals("focus");
        boolean isValid = actualMinutes >= session.getTargetMinutes();

        session.setStatus(
                isFocus ? (isValid ? FocusSession.Status.COMPLETED : FocusSession.Status.CANCELLED)
                        : FocusSession.Status.COMPLETED
        );

        return focusMapper.toResponse(focusRepository.save(session));
    }

    /**
     * Lấy danh sách phiên hôm nay
     */
    public List<FocusSession> getTodaySessions(User user) {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        return focusRepository.findByUserAndCreatedAtAfterOrderByStartedAtDesc(user, todayStart);
    }

    /**
     * Tổng phút tập trung tuần này
     */
    public int getWeeklyFocusedMinutes(User user) {
        LocalDate today = LocalDate.now();
        LocalDate start = today.with(DayOfWeek.MONDAY);
        LocalDateTime startOfWeek = start.atStartOfDay();
        LocalDateTime endOfWeek = start.plusDays(6).atTime(LocalTime.MAX);

        List<FocusSession> sessions = focusRepository.findByUserAndModeAndStatusAndStartedAtBetween(
                user, "focus", FocusSession.Status.COMPLETED, startOfWeek, endOfWeek);

        return sessions.stream().mapToInt(FocusSession::getActualMinutes).sum();
    }

    //lấy phiên
    public List<FocusSessionResponse> getCompletedSessions(User user) {
        List<FocusSession> sessions = focusRepository.findByUserAndStatusOrderByStartedAtDesc(
                user, FocusSession.Status.COMPLETED
        );
        return focusMapper.toResponseList(sessions); // convert tại đây
    }
}