package com.example.happybirthdaybot.service.data;

import com.example.happybirthdaybot.dto.ChatDto;
import com.example.happybirthdaybot.error.ApplicationException;

/**
 * Интерфейс сервиса работы с chat.
 *
 * @author Egor Mitrofanov.
 */
public interface ChatService {

    /**
     * Проверить существование чата в бд.
     *
     * @param chatId идентификатор чата.
     */
    Boolean checkChat(Long chatId);

    /**
     * Проверить существование кода для чата в бд.
     *
     * @param chatCode идентификатор чата.
     */
    Boolean checkCode(Integer chatCode);

    /**
     * Получение чата по chatId.
     *
     * @param chatId идентификатор чата.
     */
    ChatDto getChat(Long chatId) throws ApplicationException;

    /**
     * Добавление чата в бд.
     *
     * @param chatDto модель.
     */
    void createChat(ChatDto chatDto);

    /**
     * Генерация нового кода для чата.
     *
     * @return 4-х значный код.
     */
    Integer generateChatCode();

    /**
     * Удалить информацию о чате.
     *
     * @param chatId идентификатор чата.
     */
    void deleteChat(Long chatId) throws ApplicationException;

}
