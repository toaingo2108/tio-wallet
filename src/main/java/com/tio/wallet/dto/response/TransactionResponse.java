package com.tio.wallet.dto.response;

import com.tio.wallet.entity.PaymentSource;
import com.tio.wallet.entity.TransactionDirection;
import com.tio.wallet.entity.TransactionStatus;
import com.tio.wallet.entity.TransactionType;

import java.time.Instant;

public record TransactionResponse(
        Long id,
        long amountCents,
        String counterpartyName,
        TransactionType type,
        TransactionStatus status,
        TransactionDirection direction,
        PaymentSource source,
        Instant createdAt
) {
}
