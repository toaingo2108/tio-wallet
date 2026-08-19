package com.tio.wallet.service;

import com.tio.wallet.dto.response.NotificationResponse;
import com.tio.wallet.mapper.NotificationMapper;
import com.tio.wallet.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    private final NotificationMapper notificationMapper;

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsOfUser(String email) {
        return notificationRepository
                .findByRecipientEmailOrderByCreatedAtDesc(email)
                .stream()
                .map(notificationMapper::toResponse)
                .toList();
    }

}
