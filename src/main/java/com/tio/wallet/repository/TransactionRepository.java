package com.tio.wallet.repository;

import com.tio.wallet.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @EntityGraph(attributePaths = {"counterpartyWallet", "counterpartyWallet.user"})
    Page<Transaction> findByWalletIdOrderByCreatedAtDesc(Long walletId, Pageable pageable);

    // Số dư luỹ kế trước mốc thời gian (credit +, debit -)
    @Query("""
            SELECT COALESCE(SUM(CASE WHEN t.direction = com.tio.wallet.entity.TransactionDirection.CREDIT
                                THEN t.amountCents ELSE -t.amountCents END), 0)
            FROM Transaction t
            WHERE t.wallet.id = :walletId AND t.createdAt < :start
            """)
    long balanceBefore(Long walletId, Instant start);

    // Giao dịch trong khoảng [start,end]
    @EntityGraph(attributePaths = {"counterpartyWallet", "counterpartyWallet.user"})
    List<Transaction> findByWalletIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByCreatedAtAsc(
            Long walletId, Instant start, Instant end
    );
}
