package com.nlshakal.web4.service;

import com.nlshakal.web4.dto.AuthResponse;
import com.nlshakal.web4.dto.LoginRequest;
import com.nlshakal.web4.dto.SocialLoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class ReactiveAuthService {

    private final AuthService authService;

    public Mono<AuthResponse> register(LoginRequest request, String ipAddress, String userAgent) {
        return Mono.fromCallable(() -> authService.register(request, ipAddress, userAgent))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<AuthResponse> login(LoginRequest request, String ipAddress, String userAgent) {
        return Mono.fromCallable(() -> authService.login(request, ipAddress, userAgent))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<AuthResponse> socialLogin(SocialLoginRequest request, String ipAddress, String userAgent) {
        return Mono.fromCallable(() -> authService.socialLogin(request, ipAddress, userAgent))
                .subscribeOn(Schedulers.boundedElastic());
    }
}

