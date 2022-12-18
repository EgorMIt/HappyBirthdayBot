package com.example.happybirthdaybot.service.commands;

import com.example.happybirthdaybot.bot.MessageExecutor;
import com.example.happybirthdaybot.common.Answers;
import com.example.happybirthdaybot.common.Command;
import com.example.happybirthdaybot.error.ApplicationException;
import com.example.happybirthdaybot.error.ErrorDescriptions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
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
     * {@link MessageExecutor}
     */
    private final MessageExecutor messageExecutor;

    /**
     * Вызов обработчика команды
     *
     * @param command тип команды
     * @param message информация о сообщении
     */
    public void invokeCommand(Command command, Message message) throws ApplicationException {
        Message wait = messageExecutor.sendDefaultMessage(Answers.WAITING, message);

        messageExecutor.sendDefaultMessageAndDeletePrevious(
                registry.getHandlerForCommand(command)
                        .orElseThrow(ErrorDescriptions.HANDLER_NOT_FOUND::exception)
                        .invokeCommand(message), wait);
    }

}
