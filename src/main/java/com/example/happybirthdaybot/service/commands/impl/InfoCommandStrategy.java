package com.example.happybirthdaybot.service.commands.impl;

import com.example.happybirthdaybot.common.Answers;
import com.example.happybirthdaybot.common.Command;
import com.example.happybirthdaybot.dto.UserDto;
import com.example.happybirthdaybot.error.ApplicationException;
import com.example.happybirthdaybot.error.ErrorDescriptions;
import com.example.happybirthdaybot.service.commands.CommandStrategy;
import com.example.happybirthdaybot.service.data.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * Стратегия команды /info
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InfoCommandStrategy implements CommandStrategy {

    /**
     * {@link UserService}.
     */
    private final UserService userService;

    @Override
    public SendMessage invokeCommand(Message message) throws ApplicationException {
        log.info("invoke command INFO: ({}, {})", message.getChatId(), message.getFrom().getUserName());

        ErrorDescriptions.INVALID_COMMAND_IN_CHAT.throwIfFalse(message.getChat().getType().equals("private"));
        ErrorDescriptions.NO_INFO_ERROR.throwIfFalse(userService.checkUser(message.getFrom().getId()));

        UserDto userDto = userService.getUser(message.getFrom().getId());
        StringBuilder answerText;
        if (!userDto.getIsRegistered()) {
            answerText = new StringBuilder(Answers.NO_INFO);
        } else {
            LocalDate birthdayDate = userDto.getBirthdayDate();
            answerText = new StringBuilder(Answers.INFO + "Ваш день рождения:\n" + birthdayDate
                    .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)));

            if (!userDto.getWishlist().isEmpty()) {
                answerText.append("\n\nВаш список желаний:");
                for (String wish : userDto.getWishlist()) {
                    answerText.append("\n - ").append(wish);
                }
            }

            if (!userDto.getFriends().isEmpty()) {
                answerText.append("\n\nВаш список друзей:");
                for (Long friendId : userDto.getFriends()) {
                    UserDto friend = userService.getUser(friendId);
                    answerText.append("\n@").append(friend.getUserTag());
                }
            }
        }

        return SendMessage.builder()
                .chatId(message.getChatId())
                .text(answerText.toString())
                .build();
    }

    @Override
    public Command getSupportedCommand() {
        return Command.INFO;
    }

}
