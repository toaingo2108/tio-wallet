package com.tio.wallet.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String recipientEmail; // nguoi nhan thong bao = nguoi nhan tien
    private String message;

    @Column(unique = true)
    private String eventKey; // key kafka = idempotencyKey
}
