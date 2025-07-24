package com.timesphere.timesphere.repository;

import com.timesphere.timesphere.entity.FocusSession;
import com.timesphere.timesphere.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface FocusRepository extends JpaRepository<FocusSession, Long> {

    // 🧠 Lấy các phiên của người dùng
    List<FocusSession> findByUser(User user);

    // 🕒 Lấy các phiên từ thời điểm nào đó
    List<FocusSession> findByUserAndCreatedAtAfterOrderByStartedAtDesc(User user, LocalDateTime after);

    // 📅 Lấy phiên trong một ngày cụ thể
    List<FocusSession> findByUserAndStartedAtBetween(User user, LocalDateTime start, LocalDateTime end);

    // 🏆 Lấy số phiên hoàn thành
    long countByUserAndStatus(User user, FocusSession.Status status);

    // 🧘 Tuỳ: lấy các phiên dạng “focus” thành công hôm nay
    List<FocusSession> findByUserAndModeAndStatusAndCreatedAtAfter(
            User user, String mode, FocusSession.Status status, LocalDateTime startOfDay
    );

    //số thời gian hoàn thành trong tuần
    List<FocusSession> findByUserAndModeAndStatusAndStartedAtBetween(
            User user,
            String mode,
            FocusSession.Status status,
            LocalDateTime startOfWeek,
            LocalDateTime endOfWeek
    );

    //lấy phiên
    List<FocusSession> findByUserAndStatusOrderByStartedAtDesc(User user, FocusSession.Status status);

    List<FocusSession> findByUserAndModeAndStatus(User user, String mode, FocusSession.Status status);
}