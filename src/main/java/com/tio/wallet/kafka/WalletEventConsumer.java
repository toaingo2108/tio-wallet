package com.tio.wallet.kafka;

import com.tio.wallet.entity.Notification;
import com.tio.wallet.event.TransferCompletedEvent;
import com.tio.wallet.mapper.NotificationMapper;
import com.tio.wallet.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class WalletEventConsumer {
    private final NotificationRepository notificationRepository;
    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationMapper notificationMapper;

    @KafkaListener(topics = "wallet.transfer.completed", groupId = "tio-wallet")
    public void onTransferCompleted(
            @Payload String payload,
            @Header(KafkaHeaders.RECEIVED_KEY) String eventKey
    ) throws Exception {
        if (notificationRepository.existsByEventKey(eventKey)) return;

        TransferCompletedEvent event = objectMapper.readValue(payload, TransferCompletedEvent.class);

        String message = "Bạn vừa nhận $" + (event.amountCents() / 100) + " từ " + event.senderName();

        Notification saved = notificationRepository.save(Notification.builder()
                .recipientEmail(event.receiverEmail())
                .message(message)
                .eventKey(eventKey)
                .build());

        messagingTemplate.convertAndSendToUser(
                event.receiverEmail(),
                "/queue/notifications",
                notificationMapper.toResponse(saved)
        );
    }
}
