package com.nlshakal.web4.service;

import com.nlshakal.web4.dto.PointRequest;
import com.nlshakal.web4.dto.ResultResponse;
import com.nlshakal.web4.entity.ReactiveResult;
import com.nlshakal.web4.repository.ReactiveResultRepository;
import com.nlshakal.web4.util.AreaCheckUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReactiveResultService {

    private final ReactiveResultRepository reactiveResultRepository;

    public Mono<ResultResponse> checkPoint(PointRequest request, Long userId) {
        long startTime = System.nanoTime();

        boolean hit = AreaCheckUtil.checkHit(request.getX(), request.getY(), request.getR());

        long executionTime = (System.nanoTime() - startTime) / 1000;

        ReactiveResult result = new ReactiveResult(
            request.getX(),
            request.getY(),
            request.getR(),
            hit,
            LocalDateTime.now(),
            executionTime,
            userId
        );

        return reactiveResultRepository.save(result)
                .map(this::toResponse);
    }

    public Flux<ResultResponse> getUserResults(Long userId) {
        return reactiveResultRepository.findTop100ByUserIdOrderByTimestampDesc(userId)
                .map(this::toResponse);
    }

    public Mono<Void> clearUserResults(Long userId) {
        return reactiveResultRepository.deleteByUserId(userId).then();
    }

    private ResultResponse toResponse(ReactiveResult result) {
        return new ResultResponse(
            result.getId(),
            result.getX(),
            result.getY(),
            result.getR(),
            result.isHit(),
            result.getTimestamp(),
            result.getExecutionTime()
        );
    }
}

