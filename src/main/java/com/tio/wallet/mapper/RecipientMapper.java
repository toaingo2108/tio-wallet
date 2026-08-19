package com.tio.wallet.mapper;

import com.tio.wallet.dto.response.RecipientResponse;
import com.tio.wallet.entity.SavedRecipient;
import org.springframework.stereotype.Component;

@Component
public class RecipientMapper {

    public RecipientResponse toResponse(SavedRecipient r) {
        return new RecipientResponse(
                r.getRecipientEmail(),
                r.getNickname()
        );
    }
}
