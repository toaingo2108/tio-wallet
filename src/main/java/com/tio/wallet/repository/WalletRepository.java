package com.tio.wallet.repository;

import com.tio.wallet.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUserId(Long userId);

    @Query("SELECT w FROM Wallet w JOIN FETCH w.user WHERE w.user.email = :email")
    Optional<Wallet> findByUserEmail(String email);
}
