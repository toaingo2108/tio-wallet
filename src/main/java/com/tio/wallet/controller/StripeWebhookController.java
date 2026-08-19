package com.tio.wallet.controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.tio.wallet.service.StripeService;
import com.tio.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stripe/webhook")
@RequiredArgsConstructor
public class StripeWebhookController {
    private final StripeService stripeService;
    private final WalletService walletService;

    @PostMapping
    public ResponseEntity<String> handle(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sig
    ) {
        Event event;
        try {
            event = stripeService.constructEvent(payload, sig);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.badRequest().body("Invalid signature");
        }

        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElseThrow();
            String email = session.getMetadata().get("userEmail");
            long amountCents = Long.parseLong(session.getMetadata().get("amountCents"));
            walletService.depositFromStripe(email, amountCents, session.getId());
        }
        return ResponseEntity.ok("");
    }
}
