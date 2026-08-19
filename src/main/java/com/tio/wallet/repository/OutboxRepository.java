package com.tio.wallet.repository;

import com.tio.wallet.entity.OutboxEvent;
import com.tio.wallet.entity.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEvent, Long> {
    List<OutboxEvent> findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus status);

    void deleteByStatusAndSentAtBefore(OutboxStatus status, Instant cutoff);
}
