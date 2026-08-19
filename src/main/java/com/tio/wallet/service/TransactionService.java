package com.tio.wallet.service;

import com.tio.wallet.dto.response.BalanceHistoryResponse;
import com.tio.wallet.dto.response.TransactionResponse;
import com.tio.wallet.entity.*;
import com.tio.wallet.exception.BusinessException;
import com.tio.wallet.mapper.TransactionMapper;
import com.tio.wallet.repository.TransactionRepository;
import com.tio.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;

    private final TransactionMapper transactionMapper;


    private static final ZoneId ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    @Transactional(readOnly = true)
    public Page<TransactionResponse> getTransactions(String email, Pageable pageable) {
        Wallet wallet = requireWalletByEmail(email);

        return transactionRepository.findByWalletIdOrderByCreatedAtDesc(wallet.getId(), pageable)
                .map(transactionMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public BalanceHistoryResponse getBalanceHistory(String email, String month) {
        Wallet wallet = requireWalletByEmail(email);

        YearMonth ym = (month != null && !month.isBlank())
                ? YearMonth.parse(month) // "2026-08"
                : YearMonth.now(ZONE);
        Instant start = ym.atDay(1).atStartOfDay(ZONE).toInstant();
        Instant end = ym.plusMonths(1).atDay(1).atStartOfDay(ZONE).toInstant();

        long opening = transactionRepository.balanceBefore(wallet.getId(), start);
        List<TransactionResponse> transactionResponses = transactionRepository.findByWalletIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByCreatedAtAsc(
                wallet.getId(), start, end
        ).stream().map(transactionMapper::toResponse).toList();

        return new BalanceHistoryResponse(opening, transactionResponses);
    }

    private Wallet requireWalletByEmail(String email) {
        return walletRepository.findByUserEmail(email)
                .orElseThrow(() -> new BusinessException("Không tìm thấy ví"));
    }
}
