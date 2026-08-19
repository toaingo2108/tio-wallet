package com.tio.wallet.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counterparty_wallet_id")
    private Wallet counterpartyWallet;

    @Builder.Default
    @Column(nullable = false)
    private long amountCents = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionDirection direction;

    @Enumerated(EnumType.STRING)
    private PaymentSource source;

    public static Transaction deposit(Wallet from, long amountCents) {
        return Transaction.builder()
                .wallet(from)
                .amountCents(amountCents)
                .type(TransactionType.DEPOSIT)
                .direction(TransactionDirection.CREDIT)
                .status(TransactionStatus.COMPLETED)
                .source(PaymentSource.MANUAL)
                .build();
    }

    public static Transaction stripeDeposit(Wallet from, long amountCents) {
        return Transaction.builder()
                .wallet(from)
                .amountCents(amountCents)
                .type(TransactionType.DEPOSIT)
                .direction(TransactionDirection.CREDIT)
                .status(TransactionStatus.COMPLETED)
                .source(PaymentSource.STRIPE)
                .build();
    }

    public static Transaction transferCredit(Wallet from, Wallet to, long amountCents) {
        return Transaction.builder()
                .wallet(from).counterpartyWallet(to)
                .amountCents(amountCents)
                .type(TransactionType.TRANSFER)
                .direction(TransactionDirection.CREDIT)
                .status(TransactionStatus.COMPLETED)
                .build();
    }

    public static Transaction transferDebit(Wallet from, Wallet to, long amountCents) {
        return Transaction.builder()
                .wallet(from).counterpartyWallet(to)
                .amountCents(amountCents)
                .type(TransactionType.TRANSFER)
                .direction(TransactionDirection.DEBIT)
                .status(TransactionStatus.COMPLETED)
                .build();
    }
}
