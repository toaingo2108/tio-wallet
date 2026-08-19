package com.tio.wallet.repository;

import com.tio.wallet.entity.SavedRecipient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavedRecipientRepository extends JpaRepository<SavedRecipient, Long> {
    List<SavedRecipient> findByOwner_EmailOrderByUpdatedAtDesc(String ownerEmail);

    Optional<SavedRecipient> findByOwner_EmailAndRecipientEmail(String ownerEmail, String recipientEmail);
}
