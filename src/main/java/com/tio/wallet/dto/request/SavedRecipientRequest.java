package com.tio.wallet.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SavedRecipientRequest(
        @NotBlank @Email
        String email,

        @NotBlank
        String nickname
) {
}
