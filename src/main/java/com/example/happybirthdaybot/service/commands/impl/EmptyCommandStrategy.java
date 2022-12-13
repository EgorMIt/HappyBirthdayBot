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

/**
 * Стратегия команды /empty
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmptyCommandStrategy implements CommandStrategy {

    /**
     * {@link UserService}.
     */
    private final UserService userService;

    @Override
    public SendMessage invokeCommand(Message message) throws ApplicationException {
        log.info("invoke command EMPTY: ({}, {})", message.getChatId(), message.getFrom().getUserName());

        ErrorDescriptions.INVALID_COMMAND_IN_CHAT.throwIfFalse(message.getChat().getType().equals("private"));
        ErrorDescriptions.NO_INFO_ERROR.throwIfFalse(userService.checkUser(message.getFrom().getId()));

        UserDto userDto = userService.getUser(message.getChatId());
        if (userDto.getIsUpdating()) {
            userService.clearWishlist(userDto.getUserId());

            userDto.setIsUpdating(false);
            userService.updateUser(userDto);

            return SendMessage.builder()
                    .chatId(message.getChatId())
                    .text(Answers.WISHLIST_CLEAR)
                    .build();
        }
        return null;
    }

    @Override
    public Command getSupportedCommand() {
        return Command.EMPTY;
    }

}
