package com.tio.wallet.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "saved_recipients", uniqueConstraints = @UniqueConstraint(columnNames = {"owner_id", "recipient_email"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavedRecipient extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id")
    private User owner;

    @Column(nullable = false)
    private String recipientEmail;

    @Column(nullable = false)
    private String nickname;
}
