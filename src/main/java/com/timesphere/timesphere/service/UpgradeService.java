package com.timesphere.timesphere.service;

import com.timesphere.timesphere.dto.plan.SubscriptionInfoDto;
import com.timesphere.timesphere.entity.Subscription;
import com.timesphere.timesphere.entity.User;
import com.timesphere.timesphere.entity.type.PlanType;
import com.timesphere.timesphere.entity.type.Role;
import com.timesphere.timesphere.entity.type.SubscriptionStatus;
import com.timesphere.timesphere.exception.AppException;
import com.timesphere.timesphere.exception.ErrorCode;
import com.timesphere.timesphere.repository.UserRepository;
import com.timesphere.timesphere.util.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpgradeService {

    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Transactional
    public String upgradeToPremiumAndIssueTokenWithSubscription(String email, PlanType planType, String paymentId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        int durationDays = switch (planType) {
            case WEEKLY -> 7;
            case MONTHLY -> 30;
            case YEARLY -> 365;
        };

        Subscription subscription = Subscription.builder()
                .planType(planType)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(durationDays))
                .status(SubscriptionStatus.ACTIVE)
                .paymentId(paymentId)
                .user(user)
                .build();

        user.setRole(Role.PREMIUM);
        user.setSubscription(subscription);
        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return jwtUtil.generateToken(userDetails); // ✅ cấp lại token PREMIUM
    }

    public SubscriptionInfoDto getSubscriptionInfo(User user) {
        Subscription subscription = user.getSubscription();

        if (subscription == null) {
            throw new AppException(ErrorCode.NOT_JOINED_ANY_TEAM, "Người dùng chưa đăng ký gói nào");
        }

        return new SubscriptionInfoDto(
                subscription.getPlanType(),
                subscription.getStatus(),
                subscription.getStartDate(),
                subscription.getEndDate()
        );
    }

}
