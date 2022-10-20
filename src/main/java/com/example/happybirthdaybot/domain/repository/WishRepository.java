package com.example.happybirthdaybot.domain.repository;

import com.example.happybirthdaybot.domain.entity.UserEntity;
import com.example.happybirthdaybot.domain.entity.WishEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Репозиторий доступа к сущности {@link WishEntity}.
 *
 * @author Egor Mitrofanov.
 */
public interface WishRepository extends JpaRepository<WishEntity, Long> {

    /**
     * Список пользователя.
     *
     * @param user пользователь
     * @return Список {@link WishEntity}
     */
    List<WishEntity> findWishEntitiesByUser(UserEntity user);

}
