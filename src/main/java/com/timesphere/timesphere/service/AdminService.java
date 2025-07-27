package com.timesphere.timesphere.service;

import com.timesphere.timesphere.dto.admin.ChartPoint;
import com.timesphere.timesphere.dto.admin.SummaryResponse;
import com.timesphere.timesphere.exception.AppException;
import com.timesphere.timesphere.exception.ErrorCode;
import com.timesphere.timesphere.repository.FocusRepository;
import com.timesphere.timesphere.repository.TeamRepository;
import com.timesphere.timesphere.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.DayOfWeek;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final FocusRepository focusRepository;

    public SummaryResponse getDashboardSummary() {
        long totalUsers = userRepository.count();
        long totalTeams = teamRepository.count();
        long totalSessions = focusRepository.count();
        return new SummaryResponse(totalUsers, totalTeams, totalSessions);
    }

    public List<ChartPoint> getChartStats(String range, String fromDate, String toDate, Integer month, Integer year) {
        List<LocalDate> dates;

        if (fromDate != null && toDate != null) {
            try {
                LocalDate from = LocalDate.parse(fromDate);
                LocalDate to = LocalDate.parse(toDate);

                if (from.isAfter(to)) {
                    throw new AppException(ErrorCode.INVALID_KEY, "Từ ngày không được lớn hơn đến ngày.");
                }

                dates = from.datesUntil(to.plusDays(1)).collect(Collectors.toList());
            } catch (DateTimeParseException ex) {
                throw new AppException(ErrorCode.INVALID_KEY, "Định dạng ngày không hợp lệ.");
            }

        } else if (range != null) {
            switch (range.toLowerCase()) {
                case "day" -> dates = List.of(LocalDate.now());
                case "week" -> {
                    LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY);
                    dates = IntStream.range(0, 7)
                            .mapToObj(monday::plusDays)
                            .collect(Collectors.toList());
                }
                case "month" -> {
                    YearMonth targetMonth;

                    if (month != null && year != null) {
                        targetMonth = YearMonth.of(year, month); // ✅ dùng dữ liệu từ FE
                    } else {
                        targetMonth = YearMonth.now(); // fallback nếu không truyền
                    }

                    dates = IntStream.rangeClosed(1, targetMonth.lengthOfMonth())
                            .mapToObj(targetMonth::atDay)
                            .collect(Collectors.toList());
                }
                default -> throw new AppException(ErrorCode.INVALID_KEY, "Giá trị range không hợp lệ: " + range);
            }
        } else {
            throw new AppException(ErrorCode.INVALID_KEY, "Thiếu tham số range hoặc fromDate/toDate.");
        }

        return dates.stream()
                .map(date -> new ChartPoint(
                        date.toString(),
                        userRepository.countByCreatedDate(date),
                        teamRepository.countByCreatedDate(date),
                        focusRepository.countCompletedFocusByDate(date)
                ))
                .collect(Collectors.toList());
    }
}