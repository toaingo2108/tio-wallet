package com.tio.wallet.repository;

import com.tio.wallet.entity.IdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, Long> {
    boolean existsByIdempotencyKey(String idempotencyKey);

    void deleteByCreatedAtBefore(Instant cutoff);
}
