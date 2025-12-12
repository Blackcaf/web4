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
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResultService {

    private static final Logger logger = LoggerFactory.getLogger(ResultService.class);

    private final ResultRepository resultRepository;


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

        logger.debug("Point checked: userId={}, x={}, y={}, r={}, hit={}, executionTime={}μs",
                     userId, request.getX(), request.getY(), request.getR(), hit, executionTime);

        return toResponse(result);
    }

    public List<ResultResponse> getUserResults(Long userId) {
        User user = new User();
        user.setId(userId);
        List<Result> results = resultRepository.findByUserOrderByTimestampDesc(user);
        return results.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void clearUserResults(Long userId) {
        User user = new User();
        user.setId(userId);
        resultRepository.deleteByUser(user);
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

