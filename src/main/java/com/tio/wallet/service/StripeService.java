package com.tio.wallet.service;

import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class StripeService {
    private final String webhookSecret;

    public StripeService(
            @Value("${stripe.secret-key}") String key,
            @Value("${stripe.webhook-secret}") String webhookSecret
    ) {
        Stripe.apiKey = key;
        this.webhookSecret = webhookSecret;
    }

    public Event constructEvent(String payload, String sigHeader) throws SignatureVerificationException {
        return Webhook.constructEvent(payload, sigHeader, webhookSecret);
    }

    public Session createDepositCheckout(String email, long amountCents) throws StripeException {
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("https://tio.okeconde.io.vn/wallet?deposit=success")
                .setCancelUrl("https://tio.okeconde.io.vn/wallet?deposit=cancel")
                .putMetadata("userEmail", email)
                .putMetadata("amountCents", String.valueOf(amountCents))
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("usd")
                                .setUnitAmount(amountCents)
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName("Nạp tiền vào ví TIO")
                                        .build())
                                .build())
                        .build())
                .build();

        return Session.create(params);
    }
}
