package com.nlshakal.web4.repository;

import com.nlshakal.web4.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository для работы с сущностью User.
 * Предоставляет методы для поиска пользователей по username (email)
 * и проверки существования пользователя.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Находит пользователя по username (email).
     *
     * @param username email пользователя
     * @return Optional с пользователем или пустой Optional
     */
    Optional<User> findByUsername(String username);

    /**
     * Проверяет существование пользователя с данным username.
     *
     * @param username email пользователя
     * @return true если пользователь существует
     */
    boolean existsByUsername(String username);
}

