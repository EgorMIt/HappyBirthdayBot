package com.example.happybirthdaybot.service.commands.impl;

import com.example.happybirthdaybot.common.Answers;
import com.example.happybirthdaybot.common.Command;
import com.example.happybirthdaybot.domain.entity.NotificationLevel;
import com.example.happybirthdaybot.error.ApplicationException;
import com.example.happybirthdaybot.error.ErrorDescriptions;
import com.example.happybirthdaybot.service.commands.CommandStrategy;
import com.example.happybirthdaybot.service.data.UserService;
import com.example.happybirthdaybot.utils.KeyboardBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashMap;
import java.util.Map;

/**
 * Стратегия команды /notification
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationCommandStrategy implements CommandStrategy {

    /**
     * {@link UserService}.
     */
    private final UserService userService;

    /**
     * {@link KeyboardBuilder}.
     */
    private final KeyboardBuilder keyboardBuilder;

    @Override
    public SendMessage invokeCommand(Message message) throws ApplicationException {
        log.info("invoke command NOTIFICATIONS: ({}, {})", message.getChatId(), message.getFrom().getUserName());

        ErrorDescriptions.INVALID_COMMAND_IN_CHAT.throwIfFalse(message.getChat().getType().equals("private"));
        ErrorDescriptions.NO_INFO_ERROR.throwIfFalse(userService.checkUser(message.getFrom().getId()));

        String answerText;

        answerText = Answers.NOTIFICATION_CHOICE;
        SendMessage answerMessage = SendMessage.builder()
                .chatId(message.getChatId())
                .text(answerText)
                .build();

        Map<String, String> buttons = new HashMap<>();
        buttons.put("1 День", NotificationLevel.DAY.name());
        buttons.put("3 Дня", NotificationLevel.THREE_DAYS.name());
        buttons.put("Неделя", NotificationLevel.WEEK.name());
        buttons.put("Никогда", NotificationLevel.NEVER.name());

        answerMessage.setReplyMarkup(keyboardBuilder.getButtonList(buttons));

        return answerMessage;
    }

    @Override
    public Command getSupportedCommand() {
        return Command.NOTIFICATIONS;
    }

}
