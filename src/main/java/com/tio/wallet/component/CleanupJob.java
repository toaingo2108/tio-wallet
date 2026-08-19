package com.tio.wallet.component;

import com.tio.wallet.entity.OutboxStatus;
import com.tio.wallet.repository.IdempotencyKeyRepository;
import com.tio.wallet.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class CleanupJob {
    private final OutboxRepository outboxRepository;
    private final IdempotencyKeyRepository idempotencyKeyRepository;


    @Scheduled(cron = "0 0 3 * * *") // 3h sang moi ngay
    @Transactional
    public void cleanup() {
        Instant cutoff = Instant.now().minus(7, ChronoUnit.DAYS);

        outboxRepository.deleteByStatusAndSentAtBefore(OutboxStatus.SENT, cutoff);
        idempotencyKeyRepository.deleteByCreatedAtBefore(cutoff);
    }
}
