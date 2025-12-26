package com.nlshakal.web4.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReactiveCacheService {

    private static final Logger logger = LoggerFactory.getLogger(ReactiveCacheService.class);
    private static final String CACHE_PREFIX = "web4:cache:";
    private static final String USER_RESULTS_KEY = "user:results:";
    private static final String RATE_LIMIT_KEY = "rate:limit:";

    private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;

    @SuppressWarnings("unchecked")
    public <T> Mono<List<T>> getUserResults(Long userId) {
        String key = CACHE_PREFIX + USER_RESULTS_KEY + userId;
        return reactiveRedisTemplate.opsForValue().get(key)
                .cast(List.class)
                .map(list -> (List<T>) list)
                .doOnNext(list -> logger.debug("Cache hit for user results: userId={}", userId))
                .doOnError(e -> logger.debug("Cache miss for user results: userId={}", userId))
                .onErrorReturn(List.of());
    }

    public <T> Mono<Boolean> cacheUserResults(Long userId, List<T> results, Duration ttl) {
        String key = CACHE_PREFIX + USER_RESULTS_KEY + userId;
        return reactiveRedisTemplate.opsForValue().set(key, results, ttl)
                .doOnNext(success -> {
                    if (success) {
                        logger.debug("Cached user results: userId={}, count={}, ttl={}s", 
                                userId, results.size(), ttl.getSeconds());
                    }
                })
                .doOnError(e -> logger.error("Error caching user results: userId={}", userId, e))
                .onErrorReturn(false);
    }

    public Mono<Long> invalidateUserResults(Long userId) {
        String key = CACHE_PREFIX + USER_RESULTS_KEY + userId;
        return reactiveRedisTemplate.delete(key)
                .doOnNext(count -> logger.debug("Invalidated cache for user results: userId={}, deleted={}", userId, count))
                .doOnError(e -> logger.error("Error invalidating user results cache: userId={}", userId, e))
                .onErrorReturn(0L);
    }

    public Mono<Integer> getRateLimit(String ipAddress) {
        String key = CACHE_PREFIX + RATE_LIMIT_KEY + ipAddress;
        return reactiveRedisTemplate.opsForValue().get(key)
                .cast(Integer.class)
                .defaultIfEmpty(0)
                .doOnError(e -> logger.error("Error getting rate limit from cache: ip={}", ipAddress, e))
                .onErrorReturn(0);
    }

    public Mono<Long> incrementRateLimit(String ipAddress, Duration ttl) {
        String key = CACHE_PREFIX + RATE_LIMIT_KEY + ipAddress;
        return reactiveRedisTemplate.opsForValue().increment(key)
                .flatMap(count -> {
                    if (count == 1) {
                        return reactiveRedisTemplate.expire(key, ttl)
                                .thenReturn(count);
                    }
                    return Mono.just(count);
                })
                .doOnError(e -> logger.error("Error incrementing rate limit: ip={}", ipAddress, e))
                .onErrorReturn(0L);
    }

    public Mono<Long> resetRateLimit(String ipAddress) {
        String key = CACHE_PREFIX + RATE_LIMIT_KEY + ipAddress;
        return reactiveRedisTemplate.delete(key)
                .doOnNext(count -> logger.debug("Reset rate limit for IP: {}, deleted={}", ipAddress, count))
                .doOnError(e -> logger.error("Error resetting rate limit: ip={}", ipAddress, e))
                .onErrorReturn(0L);
    }

    public <T> Mono<Boolean> set(String key, T value, Duration ttl) {
        String fullKey = CACHE_PREFIX + key;
        return reactiveRedisTemplate.opsForValue().set(fullKey, value, ttl)
                .doOnNext(success -> {
                    if (success) {
                        logger.debug("Cached value: key={}, ttl={}s", key, ttl.getSeconds());
                    }
                })
                .doOnError(e -> logger.error("Error setting cache value: key={}", key, e))
                .onErrorReturn(false);
    }

    @SuppressWarnings("unchecked")
    public <T> Mono<T> get(String key, Class<T> valueType) {
        String fullKey = CACHE_PREFIX + key;
        return reactiveRedisTemplate.opsForValue().get(fullKey)
                .cast(valueType)
                .doOnError(e -> logger.error("Error getting cache value: key={}", key, e))
                .onErrorReturn(null);
    }

    public Mono<Long> delete(String key) {
        String fullKey = CACHE_PREFIX + key;
        return reactiveRedisTemplate.delete(fullKey)
                .doOnNext(count -> logger.debug("Deleted cache key: {}, deleted={}", key, count))
                .doOnError(e -> logger.error("Error deleting cache key: key={}", key, e))
                .onErrorReturn(0L);
    }

    public Mono<Boolean> exists(String key) {
        String fullKey = CACHE_PREFIX + key;
        return reactiveRedisTemplate.hasKey(fullKey)
                .doOnError(e -> logger.error("Error checking cache key existence: key={}", key, e))
                .onErrorReturn(false);
    }
}

