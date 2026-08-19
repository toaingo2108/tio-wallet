package com.tio.wallet.service;

import com.tio.wallet.dto.request.TransferRequest;
import com.tio.wallet.dto.response.BalanceResponse;
import com.tio.wallet.dto.response.WalletResponse;
import com.tio.wallet.entity.*;
import com.tio.wallet.event.TransferCompletedEvent;
import com.tio.wallet.exception.BusinessException;
import com.tio.wallet.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final IdempotencyKeyRepository idempotencyKeyRepository;
    private final OutboxRepository outboxRepository;

    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public WalletResponse getMyWallet(String email) {
        Wallet wallet = requireWalletByEmail(email);
        User user = wallet.getUser();
        return new WalletResponse(user.getName(), user.getEmail(), wallet.getBalanceCents(), wallet.getCreatedAt());
    }

    @Transactional
    public BalanceResponse deposit(String email, long amountCents) {
        Wallet wallet = requireWalletByEmail(email);

        long newBalanceCents = wallet.getBalanceCents() + amountCents;

        wallet.setBalanceCents(newBalanceCents);

        Transaction transaction = Transaction.deposit(wallet, amountCents);

        transactionRepository.save(transaction);

        return new BalanceResponse(newBalanceCents);
    }

    @Transactional
    public void depositFromStripe(String email, long amountCents, String sessionId) {
        if (idempotencyKeyRepository.existsByIdempotencyKey(sessionId)) return;

        Wallet wallet = requireWalletByEmail(email);

        wallet.setBalanceCents(wallet.getBalanceCents() + amountCents);
        transactionRepository.save(Transaction.stripeDeposit(wallet, amountCents));
        idempotencyKeyRepository.save(IdempotencyKey.builder().idempotencyKey(sessionId).build());
    }

    @Transactional
    public BalanceResponse transfer(String fromEmail, TransferRequest req, String idempotencyKey) {
        // 1. IDEMPOTENCY check
        if (idempotencyKeyRepository.existsByIdempotencyKey(idempotencyKey)) {
            Wallet wallet = requireWalletByEmail(fromEmail);

            return new BalanceResponse(wallet.getBalanceCents());
        }

        // 2. GUARD: khong tu chuyen cho minh
        if (fromEmail.equals(req.toEmail())) {
            throw new BusinessException("Không thể chuyển cho chính mình");
        }

        // 3. Load vi nguoi gui
        Wallet senderWallet = requireWalletByEmail(fromEmail);

        // 4. Loa vi nguoi nhan
        Wallet receiverWallet = requireWalletByEmail(req.toEmail());

        // 5. Check so du
        if (senderWallet.getBalanceCents() < req.amountCents()) {
            throw new BusinessException("Số dư không đủ");
        }

        // 6. Chuyen
        senderWallet.setBalanceCents(senderWallet.getBalanceCents() - req.amountCents());
        receiverWallet.setBalanceCents(receiverWallet.getBalanceCents() + req.amountCents());

        // 7. Ghi 2 dong ledger
        transactionRepository.save(Transaction.transferDebit(senderWallet, receiverWallet, req.amountCents()));
        transactionRepository.save(Transaction.transferCredit(receiverWallet, senderWallet, req.amountCents()));

        // 8. Luu idempotency key
        idempotencyKeyRepository.save(IdempotencyKey.builder().idempotencyKey(idempotencyKey).build());

        // 9. outbox
        TransferCompletedEvent event = new TransferCompletedEvent(
                senderWallet.getUser().getEmail(),
                senderWallet.getUser().getName(),
                receiverWallet.getUser().getEmail(),
                receiverWallet.getUser().getName(),
                req.amountCents()
        );

        String payload;
        try {
            payload = objectMapper.writeValueAsString(event);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        outboxRepository.save(OutboxEvent.builder()
                .aggregateType("TRANSFER")
                .aggregateId(idempotencyKey)
                .eventType("TransferCompleted")
                .payload(payload)
                .build()
        );

        // 10. Return
        return new BalanceResponse(senderWallet.getBalanceCents());
    }

    public Wallet requireWalletByEmail(String email) {
        return walletRepository.findByUserEmail(email)
                .orElseThrow(() -> new BusinessException("Không tìm thấy ví"));
    }
}
