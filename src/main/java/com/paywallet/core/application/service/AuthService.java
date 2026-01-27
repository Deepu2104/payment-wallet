package com.paywallet.core.application.service;

import com.paywallet.core.domain.model.User;
import com.paywallet.core.domain.model.Wallet;
import com.paywallet.core.domain.repository.UserRepository;
import com.paywallet.core.domain.repository.WalletRepository;
import com.paywallet.core.infrastructure.security.JwtService;
import com.paywallet.core.presentation.dto.AuthResponse;
import com.paywallet.core.presentation.dto.LoginRequest;
import com.paywallet.core.presentation.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

        private final UserRepository userRepository;
        private final WalletRepository walletRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager;
        private final UserDetailsService userDetailsService;
        private final com.paywallet.core.infrastructure.security.TokenBlacklistService tokenBlacklistService;

        @Transactional
        public AuthResponse register(RegisterRequest request) {
                if (userRepository.existsByEmail(request.getEmail())) {
                        throw new RuntimeException("Email already in use");
                }

                User user = User.builder()
                                .email(request.getEmail())
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

                UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());
                String jwtToken = jwtService.generateToken(userDetails);
                String refreshToken = jwtService.generateRefreshToken(userDetails);

                return AuthResponse.builder()
                                .accessToken(jwtToken)
                                .refreshToken(refreshToken)
                                .build();
        }

        public AuthResponse authenticate(LoginRequest request) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getEmail(),
                                                request.getPassword()));

                UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
                String jwtToken = jwtService.generateToken(userDetails);
                String refreshToken = jwtService.generateRefreshToken(userDetails);

                return AuthResponse.builder()
                                .accessToken(jwtToken)
                                .refreshToken(refreshToken)
                                .build();
        }

        public void logout(String token) {
                tokenBlacklistService.blacklistToken(token);
        }
}
