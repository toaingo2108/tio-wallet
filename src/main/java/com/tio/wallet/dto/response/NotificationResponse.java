package com.tio.wallet.dto.response;

import java.time.Instant;

public record NotificationResponse(
        Long id,
        String message,
        Instant createdAt
) {
}
