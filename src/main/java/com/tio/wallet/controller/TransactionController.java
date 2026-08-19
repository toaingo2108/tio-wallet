package com.tio.wallet.controller;

import com.tio.wallet.dto.response.ApiResponse;
import com.tio.wallet.dto.response.BalanceHistoryResponse;
import com.tio.wallet.dto.response.TransactionResponse;
import com.tio.wallet.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>>
    getTransactions(
            @NonNull Authentication authentication,
            @PageableDefault(size = 20, sort = "createdAt")
            Pageable pageable
    ) {
        String email = authentication.getName();
        return ResponseEntity.ok(ApiResponse.success(
                transactionService.getTransactions(email, pageable)
        ));
    }

    @GetMapping("/balance-history")
    public ResponseEntity<ApiResponse<BalanceHistoryResponse>>
    getBalanceHistory(
            @NonNull Authentication auth,
            @RequestParam(required = false) String month
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                transactionService.getBalanceHistory(auth.getName(), month)
        ));
    }
}
