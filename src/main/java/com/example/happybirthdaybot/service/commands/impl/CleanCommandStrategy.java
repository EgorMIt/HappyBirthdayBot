package com.example.happybirthdaybot.service.commands.impl;

import com.example.happybirthdaybot.common.Answers;
import com.example.happybirthdaybot.common.Command;
import com.example.happybirthdaybot.error.ApplicationException;
import com.example.happybirthdaybot.error.ErrorDescriptions;
import com.example.happybirthdaybot.service.commands.CommandStrategy;
import com.example.happybirthdaybot.service.data.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Стратегия команды /clean
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CleanCommandStrategy implements CommandStrategy {

    /**
     * {@link UserService}.
     */
    private final UserService userService;

    @Override
    public SendMessage invokeCommand(Message message) throws ApplicationException {
        log.info("invoke command CLEAN: ({}, {})", message.getChatId(), message.getFrom().getUserName());

        ErrorDescriptions.INVALID_COMMAND_IN_CHAT.throwIfFalse(message.getChat().getType().equals("private"));
        ErrorDescriptions.NO_INFO_ERROR.throwIfFalse(userService.checkUser(message.getFrom().getId()));

        if (userService.checkUser(message.getFrom().getId())) {
            userService.deleteUser(message.getFrom().getId());
        }
        return SendMessage.builder()
                .chatId(message.getChatId())
                .text(Answers.CLEAN_ALL)
                .build();
    }

    @Override
    public Command getSupportedCommand() {
        return Command.CLEAN;
    }

}
