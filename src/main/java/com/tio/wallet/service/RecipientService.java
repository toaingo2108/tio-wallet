package com.tio.wallet.service;

import com.tio.wallet.dto.request.SavedRecipientRequest;
import com.tio.wallet.dto.response.RecipientResponse;
import com.tio.wallet.entity.SavedRecipient;
import com.tio.wallet.exception.BusinessException;
import com.tio.wallet.exception.NotFoundResourceException;
import com.tio.wallet.mapper.RecipientMapper;
import com.tio.wallet.repository.SavedRecipientRepository;
import com.tio.wallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipientService {

    private final SavedRecipientRepository recipientRepository;
    private final UserRepository userRepository;

    private final RecipientMapper recipientMapper;

    @Transactional(readOnly = true)
    public List<RecipientResponse> list(String ownerEmail) {
        return recipientRepository.findByOwner_EmailOrderByUpdatedAtDesc(ownerEmail)
                .stream()
                .map(recipientMapper::toResponse)
                .toList();
    }

    @Transactional
    public RecipientResponse upsert(String ownerEmail, SavedRecipientRequest req) {
        String target = req.email().trim().toLowerCase();

        if (target.equalsIgnoreCase(ownerEmail)) {
            throw new BusinessException("Không thể tự lưu chính mình");
        }

        if (!userRepository.existsByEmail(target)) {
            throw new NotFoundResourceException("Người nhận không tồn tại");
        }

        SavedRecipient entity = recipientRepository.findByOwner_EmailAndRecipientEmail(ownerEmail, target)
                .orElseGet(() -> SavedRecipient.builder()
                        .owner(userRepository.findByEmail(ownerEmail).orElseThrow(() -> new NotFoundResourceException("Không tồn tại người sỡ hữu")))
                        .recipientEmail(target)
                        .build());
        entity.setNickname(req.nickname().trim());
        SavedRecipient saved = recipientRepository.save(entity);
        return recipientMapper.toResponse(saved);
    }

    @Transactional
    public void delete(String ownerEmail, String email) {
        String target = email.trim().toLowerCase();
        recipientRepository.findByOwner_EmailAndRecipientEmail(ownerEmail, target).ifPresent(recipientRepository::delete);
    }
}
