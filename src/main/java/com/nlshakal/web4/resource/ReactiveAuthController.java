package com.nlshakal.web4.resource;

import com.nlshakal.web4.dto.AuthResponse;
import com.nlshakal.web4.dto.LoginRequest;
import com.nlshakal.web4.dto.SocialLoginRequest;
import com.nlshakal.web4.service.RateLimitService;
import com.nlshakal.web4.service.ReactiveAuthService;
import com.nlshakal.web4.util.HttpRequestHelper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/api/reactive/auth")
@RequiredArgsConstructor
public class ReactiveAuthController {

    private final ReactiveAuthService reactiveAuthService;
    private final RateLimitService rateLimitService;
    private final HttpRequestHelper requestHelper;

    @PostMapping("/register")
    public Mono<ResponseEntity<AuthResponse>> register(@RequestBody LoginRequest request,
                                                       HttpServletRequest httpRequest) {
        String ipAddress = requestHelper.extractClientIP(httpRequest);
        String userAgent = requestHelper.extractUserAgent(httpRequest);

        if (!rateLimitService.isAllowed(ipAddress)) {
            return Mono.just(ResponseEntity.status(429)
                    .body(AuthResponse.builder().message("Слишком много запросов").build()));
        }

        return reactiveAuthService.register(request, ipAddress, userAgent)
                .map(this::buildSuccessResponse)
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest()
                        .body(AuthResponse.builder().message(e.getMessage()).build())));
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(@RequestBody LoginRequest request,
                                                    HttpServletRequest httpRequest) {
        String ipAddress = requestHelper.extractClientIP(httpRequest);
        String userAgent = requestHelper.extractUserAgent(httpRequest);

        if (!rateLimitService.isAllowed(ipAddress)) {
            return Mono.just(ResponseEntity.status(429)
                    .body(AuthResponse.builder().message("Слишком много запросов").build()));
        }

        return reactiveAuthService.login(request, ipAddress, userAgent)
                .map(response -> {
                    if (response.getToken() == null) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
                    }
                    rateLimitService.resetIP(ipAddress);
                    return buildSuccessResponse(response);
                })
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(AuthResponse.builder().message(e.getMessage()).build())));
    }

    @PostMapping("/social-login")
    public Mono<ResponseEntity<AuthResponse>> socialLogin(@RequestBody SocialLoginRequest request,
                                                          HttpServletRequest httpRequest) {
        String ipAddress = requestHelper.extractClientIP(httpRequest);
        String userAgent = requestHelper.extractUserAgent(httpRequest);

        return reactiveAuthService.socialLogin(request, ipAddress, userAgent)
                .map(this::buildSuccessResponse)
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest()
                        .body(AuthResponse.builder().message(e.getMessage()).build())));
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<Void>> logout() {
        ResponseCookie tokenCookie = ResponseCookie.from("auth_token", "")
                .path("/")
                .maxAge(Duration.ZERO)
                .httpOnly(true)
                .sameSite("Strict")
                .build();

        ResponseCookie usernameCookie = ResponseCookie.from("username", "")
                .path("/")
                .maxAge(Duration.ZERO)
                .sameSite("Strict")
                .build();

        return Mono.just(ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, tokenCookie.toString(), usernameCookie.toString())
                .build());
    }

    private ResponseEntity<AuthResponse> buildSuccessResponse(AuthResponse response) {
        ResponseCookie tokenCookie = ResponseCookie.from("auth_token", response.getToken())
                .path("/")
                .maxAge(Duration.ofDays(1))
                .httpOnly(true)
                .sameSite("Strict")
                .build();

        ResponseCookie usernameCookie = ResponseCookie.from("username", response.getUsername())
                .path("/")
                .maxAge(Duration.ofDays(1))
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, tokenCookie.toString(), usernameCookie.toString())
                .body(response);
    }
}

