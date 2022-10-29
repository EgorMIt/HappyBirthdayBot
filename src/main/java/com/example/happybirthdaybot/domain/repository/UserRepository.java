package com.example.happybirthdaybot.domain.repository;

import com.example.happybirthdaybot.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий доступа к сущности {@link UserEntity}.
 *
 * @author Egor Mitrofanov.
 */
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Поиск пользователя по идентификатору.
     *
     * @param userID идентификатор
     * @return {@link UserEntity}
     */
    Optional<UserEntity> findUserEntityByUserId(Long userID);

    /**
     * Поиск пользователя по имени пользователя.
     *
     * @param userTag имя пользователя
     * @return {@link UserEntity}
     */
    Optional<UserEntity> findUserEntityByUserTag(String userTag);

    /**
     * Поиск пользователей по дню рождения.
     *
     * @param day   день рождения
     * @param month месяц рождения
     * @return Список {@link UserEntity}
     */
    List<UserEntity> findUserEntitiesByDayAndMonth(Integer day, Integer month);

}
