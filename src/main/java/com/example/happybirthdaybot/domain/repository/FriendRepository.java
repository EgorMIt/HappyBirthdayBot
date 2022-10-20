package com.example.happybirthdaybot.domain.repository;

import com.example.happybirthdaybot.domain.entity.FriendEntity;
import com.example.happybirthdaybot.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Репозиторий доступа к сущности {@link FriendEntity}.
 *
 * @author Egor Mitrofanov.
 */
public interface FriendRepository extends JpaRepository<FriendEntity, Long> {

    /**
     * Поиск пользователей у кого в друзьях есть friendId.
     *
     * @param friend идентификатор
     * @return {@link FriendEntity}
     */
    List<FriendEntity> findFriendEntitiesByFriend(UserEntity friend);

}
