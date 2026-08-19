package com.tio.wallet.mapper;

import com.tio.wallet.dto.response.NotificationResponse;
import com.tio.wallet.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getMessage(),
                notification.getCreatedAt()
        );
    }
}
