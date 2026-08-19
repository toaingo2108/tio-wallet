package com.tio.wallet.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.tio.wallet.dto.request.DepositRequest;
import com.tio.wallet.dto.request.TransferRequest;
import com.tio.wallet.dto.response.ApiResponse;
import com.tio.wallet.dto.response.BalanceResponse;
import com.tio.wallet.dto.response.WalletResponse;
import com.tio.wallet.service.StripeService;
import com.tio.wallet.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final StripeService stripeService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<WalletResponse>>
    myWallet(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(ApiResponse.success(walletService.getMyWallet(email)));
    }

    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<BalanceResponse>>
    deposit(Authentication authentication,
            @Valid @RequestBody DepositRequest request
    ) {
        String email = authentication.getName();
        return ResponseEntity.ok(ApiResponse.success(walletService.deposit(email, request.amountCents())));
    }

    @PostMapping("/deposit/checkout")
    public ResponseEntity<ApiResponse<Map<String, String>>>
    depositCheckout(
            Authentication auth,
            @RequestBody DepositRequest req
    ) throws StripeException {
        Session session = stripeService.createDepositCheckout(auth.getName(), req.amountCents());
        return ResponseEntity.ok(ApiResponse.success(Map.of("url", session.getUrl())));
    }

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<BalanceResponse>>
    transfer(
            Authentication auth,
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody TransferRequest req
    ) {
        BalanceResponse res = walletService.transfer(auth.getName(), req, idempotencyKey);
        return ResponseEntity.ok(ApiResponse.success(res));
    }

}
