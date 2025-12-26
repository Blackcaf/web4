package com.nlshakal.web4.repository;

import com.nlshakal.web4.entity.ReactiveResult;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ReactiveResultRepository extends ReactiveCrudRepository<ReactiveResult, Long> {

    @Query("SELECT * FROM results WHERE user_id = :userId ORDER BY timestamp DESC FETCH FIRST 100 ROWS ONLY")
    Flux<ReactiveResult> findTop100ByUserIdOrderByTimestampDesc(Long userId);

    @Query("DELETE FROM results WHERE user_id = :userId")
    Flux<Void> deleteByUserId(Long userId);
}

