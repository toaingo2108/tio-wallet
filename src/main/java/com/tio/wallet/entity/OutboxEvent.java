package com.tio.wallet.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "outbox_events")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEvent extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String aggregateType;

    private String aggregateId;

    private String eventType;

    @Column(columnDefinition = "text")
    private String payload;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutboxStatus status = OutboxStatus.PENDING;

    private Instant sentAt;

    public void markSent() {
        status = OutboxStatus.SENT;
        sentAt = Instant.now();
    }

}
