package com.nlshakal.web4.repository;

import com.nlshakal.web4.entity.Result;
import com.nlshakal.web4.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {
    List<Result> findByUserOrderByTimestampDesc(User user);
    void deleteByUser(User user);
}

