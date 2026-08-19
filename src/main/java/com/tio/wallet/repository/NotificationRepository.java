package com.tio.wallet.repository;

import com.tio.wallet.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    boolean existsByEventKey(String eventKey);

    List<Notification> findByRecipientEmailOrderByCreatedAtDesc(String email);
}
