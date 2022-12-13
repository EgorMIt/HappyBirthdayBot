package com.example.happybirthdaybot.service.commands.impl;

import com.example.happybirthdaybot.bot.NotificationPostman;
import com.example.happybirthdaybot.common.Command;
import com.example.happybirthdaybot.error.ApplicationException;
import com.example.happybirthdaybot.service.commands.CommandStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Стратегия команды /admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminCommandStrategy implements CommandStrategy {

    /**
     * {@link NotificationPostman}.
     */
    private final NotificationPostman postman;

    @Override
    public SendMessage invokeCommand(Message message) throws ApplicationException {
        log.info("invoke command ADMIN: ({}, {})", message.getChatId(), message.getFrom().getUserName());

        String answerText = "";
        if (message.getChat().getType().equals("private") && message.getChatId() == 402975103) {
            postman.CheckBirthdays();
            answerText = "Начинаю рассылку уведомлений...";
        }
        return SendMessage.builder()
                .chatId(message.getChatId())
                .text(answerText)
                .build();
    }

    @Override
    public Command getSupportedCommand() {
        return Command.ADMIN;
    }

}
