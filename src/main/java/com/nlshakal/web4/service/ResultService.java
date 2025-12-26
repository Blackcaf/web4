package com.nlshakal.web4.service;

import com.nlshakal.web4.dto.PointRequest;
import com.nlshakal.web4.dto.ResultResponse;
import com.nlshakal.web4.entity.Result;
import com.nlshakal.web4.entity.User;
import com.nlshakal.web4.repository.ResultRepository;
import com.nlshakal.web4.util.AreaCheckUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResultService {

    private static final Logger logger = LoggerFactory.getLogger(ResultService.class);
    private static final Duration CACHE_TTL = Duration.ofMinutes(5);

    private final ResultRepository resultRepository;
    private final CacheService cacheService;


    public ResultResponse checkPoint(PointRequest request, Long userId) {
        long startTime = System.nanoTime();

        User user = new User();
        user.setId(userId);

        boolean hit = AreaCheckUtil.checkHit(request.getX(), request.getY(), request.getR());

        long executionTime = (System.nanoTime() - startTime) / 1000;

        Result result = new Result(
            request.getX(),
            request.getY(),
            request.getR(),
            hit,
            LocalDateTime.now(),
            executionTime,
            user
        );

        result = resultRepository.save(result);

        cacheService.invalidateUserResults(userId);

        logger.debug("Point checked: userId={}, x={}, y={}, r={}, hit={}, executionTime={}μs",
                     userId, request.getX(), request.getY(), request.getR(), hit, executionTime);

        return toResponse(result);
    }

    public List<ResultResponse> getUserResults(Long userId) {
        logger.debug("getUserResults called for userId={}", userId);

        Optional<List<ResultResponse>> cached = cacheService.getUserResults(userId, ResultResponse.class);
        if (cached.isPresent()) {
            logger.info("Returning cached results for userId={}, count={}", userId, cached.get().size());
            return cached.get();
        }

        User user = new User();
        user.setId(userId);
        List<Result> results = resultRepository.findTop100ByUserOrderByTimestampDesc(user);
        List<ResultResponse> responseList = results.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        cacheService.cacheUserResults(userId, responseList, CACHE_TTL);
        logger.info("Loaded and cached results from DB for userId={}, count={}", userId, responseList.size());

        return responseList;
    }

    @Transactional
    public void clearUserResults(Long userId) {
        logger.info("Starting clearUserResults for userId={}", userId);
        resultRepository.deleteByUserId(userId);
        logger.info("Deleted results from DB for userId={}", userId);
        cacheService.invalidateUserResults(userId);
        logger.info("Invalidated cache for userId={}", userId);
    }

    public void updateCacheAfterClear(Long userId) {
        cacheService.cacheUserResults(userId, List.of(), CACHE_TTL);
        logger.info("Updated cache with empty list for userId={}", userId);
    }

    private ResultResponse toResponse(Result result) {
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
