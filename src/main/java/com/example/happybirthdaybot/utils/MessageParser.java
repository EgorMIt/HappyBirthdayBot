package com.example.happybirthdaybot.utils;

import com.example.happybirthdaybot.common.Command;
import com.example.happybirthdaybot.domain.entity.NotificationLevel;
import com.example.happybirthdaybot.dto.UserDto;
import com.example.happybirthdaybot.error.ApplicationException;
import com.example.happybirthdaybot.error.ErrorDescriptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Парсинг сообщений от пользователей.
 *
 * @author Egor Mitrofanov.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageParser {

    /**
     * Проверка на присутствие команды.
     *
     * @param text текст сообщения.
     * @return модель {@link UserDto}.
     */
    public Command checkForCommand(String text) throws ApplicationException {
        String command = text.split("/")[1];
        if (command.contains("@")) {
            command = command.split("@")[0];
        }
        if (command.contains(" ")) {
            command = command.split(" ")[0];
        }
        try {
            return Command.valueOf(command.toUpperCase().trim());
        } catch (Exception e) {
            log.info("Invalid command!");
            throw ErrorDescriptions.INVALID_COMMAND.exception();
        }
    }

    /**
     * Проверка на присутствие Notification Level.
     *
     * @param text текст сообщения.
     * @return модель {@link UserDto}.
     */
    public Boolean checkForNotification(String text) throws ApplicationException {
        try {
            NotificationLevel.valueOf(text.toUpperCase().trim());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Проверка на присутствие даты в сообщении.
     *
     * @param text текст сообщения.
     */
    public Boolean hasDate(String text) {
        String regEx = "[0-9]{2}.[0-9]{2}.[0-9]{4}";
        return text.trim().matches(regEx);
    }

    /**
     * Проверка на присутствие кода чата в сообщении.
     *
     * @param text текст сообщения.
     */
    public Boolean hasCode(String text) {
        String regEx = "[0-9]*";
        return text.trim().matches(regEx);
    }

    /**
     * Проверка на присутствие тега пользователя.
     *
     * @param text текст сообщения.
     */
    public Boolean hasUserTag(String text) {
        return text.charAt(0) == '@';
    }

}
