package com.tio.wallet.service;

import com.tio.wallet.dto.request.LoginRequest;
import com.tio.wallet.dto.request.RegisterRequest;
import com.tio.wallet.dto.response.AuthResponse;
import com.tio.wallet.entity.User;
import com.tio.wallet.entity.Wallet;
import com.tio.wallet.exception.DuplicateResourceException;
import com.tio.wallet.exception.InvalidCredentialsException;
import com.tio.wallet.repository.UserRepository;
import com.tio.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponse register(@NonNull RegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new DuplicateResourceException("Email đã được dùng");
        }

        User user = User.builder()
                .email(req.email())
                .name(req.name())
                .passwordHash(passwordEncoder.encode(req.password()))
                .build();

        User saved = userRepository.save(user);

        Wallet wallet = walletRepository.save(
                Wallet.builder().user(saved).build()
        );

        return new AuthResponse(
                jwtService.generateToken(saved.getEmail())
        );
    }

    @Transactional
    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new InvalidCredentialsException("Email hoặc Password không đúng, vui lòng thử lại"));

        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Email hoặc Password không đúng, vui lòng thử lại");
        }

        return new AuthResponse(
                jwtService.generateToken(user.getEmail())
        );
    }
}
