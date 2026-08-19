package com.tio.wallet.mapper;

import com.tio.wallet.dto.response.TransactionResponse;
import com.tio.wallet.entity.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {
    public TransactionResponse toResponse(Transaction t) {
        return new TransactionResponse(
                t.getId(),
                t.getAmountCents(),
                t.getCounterpartyWallet() != null // Bẫy 1: deposit không có đối tác
                        ? t.getCounterpartyWallet().getUser().getName() // bẫy 2: N+1 query
                        : null,
                t.getType(),
                t.getStatus(),
                t.getDirection(),
                t.getSource(),
                t.getCreatedAt()
        );
    }
}
