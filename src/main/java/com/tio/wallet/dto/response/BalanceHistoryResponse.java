package com.tio.wallet.dto.response;

import java.util.List;

public record BalanceHistoryResponse(
        long openingBalanceCents, // số dư ngay trước tháng -> để làm mốc
        List<TransactionResponse> transactions
) {
}
