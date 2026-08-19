package com.tio.wallet.event;

public record TransferCompletedEvent(
        String senderEmail,
        String senderName,
        String receiverEmail,
        String receiverName,
        long amountCents
) {
}
