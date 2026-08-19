package com.tio.wallet.dto.response;

import java.time.Instant;

public record WalletResponse(
        String name,
        String email,
        long balanceCents,
        Instant createdAt
) {
}
