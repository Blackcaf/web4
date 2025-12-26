package com.nlshakal.web4.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CacheService {

    private static final Logger logger = LoggerFactory.getLogger(CacheService.class);
    private static final String CACHE_PREFIX = "web4:cache:";
    private static final String USER_RESULTS_KEY = "user:results:";
    private static final String RATE_LIMIT_KEY = "rate:limit:";

    private final RedisTemplate<String, Object> redisTemplate;

    public <T> Optional<List<T>> getUserResults(Long userId, Class<T> valueType) {
        try {
            String key = CACHE_PREFIX + USER_RESULTS_KEY + userId;
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached != null) {
                logger.debug("Cache hit for user results: userId={}", userId);
                if (cached instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<T> result = (List<T>) cached;
                    return Optional.of(result);
                }
            }
            logger.debug("Cache miss for user results: userId={}", userId);
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Error getting user results from cache: userId={}", userId, e);
            return Optional.empty();
        }
    }

    public <T> void cacheUserResults(Long userId, List<T> results, Duration ttl) {
        try {
            String key = CACHE_PREFIX + USER_RESULTS_KEY + userId;
            redisTemplate.opsForValue().set(key, results, ttl.toMillis(), TimeUnit.MILLISECONDS);
            logger.info("Cached user results: userId={}, count={}, ttl={}s", userId, results.size(), ttl.getSeconds());
        } catch (Exception e) {
            logger.error("Error caching user results: userId={}", userId, e);
        }
    }

    public void invalidateUserResults(Long userId) {
        try {
            String key = CACHE_PREFIX + USER_RESULTS_KEY + userId;
            Boolean deleted = redisTemplate.delete(key);
            logger.info("Invalidated cache for user results: userId={}, deleted={}", userId, deleted);
        } catch (Exception e) {
            logger.error("Error invalidating user results cache: userId={}", userId, e);
        }
    }

    public Optional<Integer> getRateLimit(String ipAddress) {
        try {
            String key = CACHE_PREFIX + RATE_LIMIT_KEY + ipAddress;
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached != null) {
                if (cached instanceof Integer) {
                    return Optional.of((Integer) cached);
                } else if (cached instanceof String) {
                    return Optional.of(Integer.parseInt((String) cached));
                }
            }
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Error getting rate limit from cache: ip={}", ipAddress, e);
            return Optional.empty();
        }
    }

    public long incrementRateLimit(String ipAddress, Duration ttl) {
        try {
            String key = CACHE_PREFIX + RATE_LIMIT_KEY + ipAddress;
            Long count = redisTemplate.opsForValue().increment(key);
            if (count != null && count == 1) {
                redisTemplate.expire(key, ttl.toMillis(), TimeUnit.MILLISECONDS);
            }
            return count != null ? count : 0;
        } catch (Exception e) {
            logger.error("Error incrementing rate limit: ip={}", ipAddress, e);
            return 0;
        }
    }

    public void resetRateLimit(String ipAddress) {
        try {
            String key = CACHE_PREFIX + RATE_LIMIT_KEY + ipAddress;
            redisTemplate.delete(key);
            logger.debug("Reset rate limit for IP: {}", ipAddress);
        } catch (Exception e) {
            logger.error("Error resetting rate limit: ip={}", ipAddress, e);
        }
    }

    public <T> void set(String key, T value, Duration ttl) {
        try {
            String fullKey = CACHE_PREFIX + key;
            redisTemplate.opsForValue().set(fullKey, value, ttl.toMillis(), TimeUnit.MILLISECONDS);
            logger.debug("Cached value: key={}, ttl={}s", key, ttl.getSeconds());
        } catch (Exception e) {
            logger.error("Error setting cache value: key={}", key, e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String key, Class<T> valueType) {
        try {
            String fullKey = CACHE_PREFIX + key;
            Object cached = redisTemplate.opsForValue().get(fullKey);
            if (cached != null && valueType.isInstance(cached)) {
                return Optional.of((T) cached);
            }
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Error getting cache value: key={}", key, e);
            return Optional.empty();
        }
    }

    public void delete(String key) {
        try {
            String fullKey = CACHE_PREFIX + key;
            redisTemplate.delete(fullKey);
            logger.debug("Deleted cache key: {}", key);
        } catch (Exception e) {
            logger.error("Error deleting cache key: key={}", key, e);
        }
    }

    public boolean exists(String key) {
        try {
            String fullKey = CACHE_PREFIX + key;
            Boolean exists = redisTemplate.hasKey(fullKey);
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            logger.error("Error checking cache key existence: key={}", key, e);
            return false;
        }
    }
}

