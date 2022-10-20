package com.example.happybirthdaybot.domain.repository;

import com.example.happybirthdaybot.domain.entity.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Репозиторий доступа к сущности {@link ChatEntity}.
 *
 * @author Egor Mitrofanov.
 */
public interface ChatRepository extends JpaRepository<ChatEntity, Long> {

    /**
     * Поиск чата по идентификатору чата.
     *
     * @param chatId идентификатор
     * @return {@link ChatEntity}
     */
    Optional<ChatEntity> findChatEntityByChatId(Long chatId);

    /**
     * Поиск чата по коду.
     *
     * @param chatCode код
     * @return {@link ChatEntity}
     */
    Optional<ChatEntity> findChatEntityByChatCode(Integer chatCode);

}
