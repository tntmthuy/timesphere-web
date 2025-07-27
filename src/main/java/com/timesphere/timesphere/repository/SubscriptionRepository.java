package com.timesphere.timesphere.repository;

import com.timesphere.timesphere.entity.Subscription;
import com.timesphere.timesphere.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {
    List<Subscription> findByUser(User user);
}

