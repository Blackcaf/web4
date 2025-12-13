package com.nlshakal.web4.repository;

import com.nlshakal.web4.entity.Result;
import com.nlshakal.web4.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository для работы с сущностью Result.
 * Предоставляет методы для получения и удаления результатов проверок пользователя.
 */
@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {
    /**
     * Находит все результаты пользователя, отсортированные по времени (новые первыми).
     *
     * @param user пользователь
     * @return список результатов в обратном хронологическом порядке
     */
    List<Result> findByUserOrderByTimestampDesc(User user);

    /**
     * Удаляет все результаты указанного пользователя.
     * Метод должен вызываться внутри транзакции (@Transactional).
     *
     * @param user пользователь
     */
    void deleteByUser(User user);
}

