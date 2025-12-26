package com.nlshakal.web4.repository;

import com.nlshakal.web4.entity.Result;
import com.nlshakal.web4.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {
    List<Result> findByUserOrderByTimestampDesc(User user);
    List<Result> findTop100ByUserOrderByTimestampDesc(User user);

    @Modifying
    @Query("DELETE FROM Result r WHERE r.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    void deleteByUser(User user);
}
