package com.nlshakal.web4.resource;

import com.nlshakal.web4.dto.PointRequest;
import com.nlshakal.web4.dto.ResultResponse;
import com.nlshakal.web4.service.RateLimitService;
import com.nlshakal.web4.service.ReactiveResultService;
import com.nlshakal.web4.util.HttpRequestHelper;
import com.nlshakal.web4.util.TokenExtractor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/api/reactive/results")
@RequiredArgsConstructor
public class ReactiveResultController {

    private final ReactiveResultService reactiveResultService;
    private final TokenExtractor tokenExtractor;
    private final RateLimitService rateLimitService;
    private final HttpRequestHelper requestHelper;


    @PostMapping("/check")
    public Mono<ResponseEntity<ResultResponse>> checkPoint(@Valid @RequestBody PointRequest request,
                                                           HttpServletRequest httpRequest) {
        String ipAddress;
        Long userId;
        try {
            ipAddress = requestHelper.extractClientIP(httpRequest);
            userId = tokenExtractor.extractUserId(httpRequest);
        } catch (Exception e) {
            if (e.getMessage() != null && (e.getMessage().contains("Missing") || e.getMessage().contains("Invalid"))) {
                return Mono.just(ResponseEntity.status(401).build());
            }
            throw e;
        }

        return Mono.fromCallable(() -> {
                    if (!rateLimitService.isAllowed(ipAddress)) {
                        return ResponseEntity.status(429).<ResultResponse>build();
                    }
                    return null;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(errorResponse -> {
                    if (errorResponse != null) {
                        return Mono.just(errorResponse);
                    }
                    return reactiveResultService.checkPoint(request, userId)
                            .map(ResponseEntity::ok);
                })
                .onErrorResume(e -> {
                    if (e.getMessage() != null && (e.getMessage().contains("Missing") || e.getMessage().contains("Invalid"))) {
                        return Mono.just(ResponseEntity.status(401).build());
                    }
                    return Mono.error(e);
                });
    }

    @GetMapping
    public Flux<ResultResponse> getResults(HttpServletRequest httpRequest) {
        Long userId = tokenExtractor.extractUserId(httpRequest);
        return reactiveResultService.getUserResults(userId);
    }

    @DeleteMapping
    public Mono<ResponseEntity<Void>> clearResults(HttpServletRequest httpRequest) {
        Long userId = tokenExtractor.extractUserId(httpRequest);
        return reactiveResultService.clearUserResults(userId)
                .map(v -> ResponseEntity.ok().build());
    }
}

