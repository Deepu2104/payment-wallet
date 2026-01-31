package com.paywallet.core.application.service;

import com.paywallet.core.domain.model.User;
import com.paywallet.core.domain.model.Wallet;
import com.paywallet.core.domain.repository.UserRepository;
import com.paywallet.core.domain.repository.WalletRepository;
import com.paywallet.core.infrastructure.security.RedisTokenService;
import com.paywallet.core.presentation.dto.AuthResponse;
import com.paywallet.core.presentation.dto.LoginRequest;
import com.paywallet.core.presentation.dto.RegisterRequest;
import com.paywallet.core.domain.exception.DuplicateResourceException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

        private final UserRepository userRepository;
        private final WalletRepository walletRepository;
        private final PasswordEncoder passwordEncoder;
        private final RedisTokenService redisTokenService;
        private final AuthenticationManager authenticationManager;

        private static final Duration TOKEN_TTL = Duration.ofHours(24);

        @Transactional
        public AuthResponse register(RegisterRequest request) {
                if (userRepository.existsByEmail(request.getEmail())) {
                        throw new DuplicateResourceException("Email already in use", "EMAIL_EXISTS");
                }

                User user = User.builder()
                                .email(request.getEmail())
                                .name(request.getName())
                                .passwordHash(passwordEncoder.encode(request.getPassword()))
                                .roles(Set.of("ROLE_USER"))
                                .isActive(true)
                                .build();

                User savedUser = userRepository.save(user);

                // Create initial wallet
                Wallet wallet = Wallet.builder()
                                .userId(savedUser.getId())
                                .balance(BigDecimal.ZERO)
                                .currency("USD")
                                .build();

                walletRepository.save(wallet);

                String token = UUID.randomUUID().toString();
                redisTokenService.saveToken(token, savedUser.getEmail(), TOKEN_TTL);

                return AuthResponse.builder()
                                .accessToken(token)
                                .build();
        }

        public AuthResponse authenticate(LoginRequest request) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getEmail(),
                                                request.getPassword()));

                User user = userRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new com.paywallet.core.domain.exception.ResourceNotFoundException(
                                                "User not found"));

                String token = UUID.randomUUID().toString();
                redisTokenService.saveToken(token, user.getEmail(), TOKEN_TTL);

                return AuthResponse.builder()
                                .accessToken(token)
                                .build();
        }

        public void logout(String accessToken) {
                redisTokenService.deleteToken(accessToken);
        }
}
