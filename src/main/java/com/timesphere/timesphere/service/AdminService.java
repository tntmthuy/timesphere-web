package com.timesphere.timesphere.service;

import com.timesphere.timesphere.dto.admin.ChartPoint;
import com.timesphere.timesphere.dto.admin.SummaryResponse;
import com.timesphere.timesphere.dto.admin.TeamDto;
import com.timesphere.timesphere.dto.admin.UserSummaryDto;
import com.timesphere.timesphere.dto.member.TeamMemberDTO;
import com.timesphere.timesphere.entity.TeamWorkspace;
import com.timesphere.timesphere.entity.User;
import com.timesphere.timesphere.entity.type.Role;
import com.timesphere.timesphere.exception.AppException;
import com.timesphere.timesphere.exception.ErrorCode;
import com.timesphere.timesphere.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
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
    private final UserMapper userMapper;
    private final TeamRepository teamWorkspaceRepository;

    private final TaskRepository taskRepo;
    private final TaskCommentRepository commentRepo;
    private final AttachmentRepository attachmentRepository;

    //phụ thuộc
    private final TeamMemberRepository teamMemberRepository;
    private final TokenRepository tokenRepository;

    //danh sách người dùng
    public List<UserSummaryDto> getAllUsers() {
        return userRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(userMapper::toSummaryDto)
                .toList();
    }

    public void updateUserRole(String userId, Role role) {
        if (role == null) {
            throw new AppException(ErrorCode.ROLE_REQUIRED);
        }

        if (role != Role.FREE && role != Role.PREMIUM) {
            throw new AppException(ErrorCode.ROLE_NOT_SUPPORTED);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (user.getRole() == Role.ADMIN) {
            throw new AppException(ErrorCode.CANNOT_CHANGE_OWN_ROLE, "Không thể thay đổi vai trò của ADMIN.");
        }

        user.setRole(role);
        userRepository.save(user);
    }


    //xóa
    @Transactional
    public void deleteUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (user.getRole() == Role.ADMIN) {
            throw new AppException(ErrorCode.CANNOT_DELETE_ADMIN);
        }

        // 👉 Xóa quan hệ trước khi xóa user
        teamMemberRepository.deleteAllByUser(user);
        tokenRepository.deleteByUserId(user.getId());

        userRepository.delete(user);
    }

    public SummaryResponse getDashboardSummary() {
        long totalUsers = userRepository.count();
        long totalTeams = teamRepository.count();
        long totalSessions = focusRepository.count();
        return new SummaryResponse(totalUsers, totalTeams, totalSessions);
    }

    //danh sách team
    public List<TeamDto> getAllTeamsWithMembers() {
        List<TeamWorkspace> teams = teamWorkspaceRepository.findAll();

        return teams.stream().map(team -> {
            List<TeamMemberDTO> memberDTOs = team.getMembers().stream()
                    .map(tm -> TeamMemberDTO.builder()
                            .memberId(tm.getId())
                            .userId(tm.getUser().getId())
                            .teamId(tm.getTeam().getId())
                            .fullName(tm.getUser().getFullName())
                            .email(tm.getUser().getEmail())
                            .avatarUrl(tm.getUser().getAvatarUrl())
                            .build()
                    ).toList();

            int taskCount = (int) taskRepo.countByTeam(team);
            int commentCount = (int) commentRepo.countByTeam(team); // ✅ dùng query đếm
            int fileCount = (int) attachmentRepository.countByTeamId(team.getId(), null);

            return new TeamDto(
                    team.getId(),
                    team.getTeamName(),
                    team.getDescription(),
                    team.getCreatedBy() != null ? team.getCreatedBy().getFullName() : null,
                    memberDTOs,
                    taskCount,
                    commentCount,
                    fileCount
            );
        }).toList();
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