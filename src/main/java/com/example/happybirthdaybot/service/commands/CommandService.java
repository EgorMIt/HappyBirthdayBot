package com.example.happybirthdaybot.service.commands;

import com.example.happybirthdaybot.common.Command;
import com.example.happybirthdaybot.error.ApplicationException;
import com.example.happybirthdaybot.error.ErrorDescriptions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Обработчик комманд
 *
 * @author Egor Mitrofanov.
 */
@Component
@RequiredArgsConstructor
public class CommandService {

    /**
     * {@link CommandStrategyRegistry}
     */
    private final CommandStrategyRegistry registry;

    /**
     * Вызов обработчика команды
     *
     * @param command тип команды
     * @param message информация о сообщении
     * @return отправляемое сообщение
     */
    public SendMessage invokeCommand(Command command, Message message) throws ApplicationException {
        return registry.getHandlerForCommand(command)
                .orElseThrow(ErrorDescriptions.HANDLER_NOT_FOUND::exception)
                .invokeCommand(message);
    }

}
