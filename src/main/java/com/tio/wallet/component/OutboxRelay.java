package com.tio.wallet.component;

import com.tio.wallet.entity.OutboxEvent;
import com.tio.wallet.entity.OutboxStatus;
import com.tio.wallet.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxRelay {
    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 5000) // 5s quet 1 lan
    @Transactional
    public void publishPending() {
        List<OutboxEvent> batch = outboxRepository.findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);
        for (OutboxEvent e : batch) {
            try {
                kafkaTemplate
                        .send("wallet.transfer.completed",
                                e.getAggregateId(),
                                e.getPayload())
                        .get();
                e.markSent();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

    }
}
