package com.example.happybirthdaybot.service.commands;

import com.example.happybirthdaybot.common.Command;
import com.example.happybirthdaybot.error.ApplicationException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Интерфейс для обработки команд
 *
 * @author Egor Mitrofanov.
 */
public interface CommandStrategy {

    /**
     * Запуск команды
     *
     * @param message информация о сообщении
     * @return возвращаемое сообщение
     */
    SendMessage invokeCommand(Message message) throws ApplicationException;

    /**
     * Поддерживаемая комманда
     *
     * @return тип комманды {@link Command}
     */
    Command getSupportedCommand();

}
