package com.example.happybirthdaybot.service.commands.impl;

import com.example.happybirthdaybot.bot.NotificationPostman;
import com.example.happybirthdaybot.common.Answers;
import com.example.happybirthdaybot.common.Command;
import com.example.happybirthdaybot.dto.UserDto;
import com.example.happybirthdaybot.error.ApplicationException;
import com.example.happybirthdaybot.error.ErrorDescriptions;
import com.example.happybirthdaybot.service.commands.CommandStrategy;
import com.example.happybirthdaybot.service.data.ChatService;
import com.example.happybirthdaybot.service.data.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Стратегия команды /soon
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SoonCommandStrategy implements CommandStrategy {

    /**
     * {@link UserService}.
     */
    private final UserService userService;

    /**
     * {@link ChatService}.
     */
    private final ChatService chatService;

    /**
     * {@link NotificationPostman}.
     */
    private final NotificationPostman postman;

    @Override
    public SendMessage invokeCommand(Message message) throws ApplicationException {
        log.info("invoke command SOON: ({}, {})", message.getChatId(), message.getFrom().getUserName());

        ErrorDescriptions.INVALID_COMMAND_IN_CHAT.throwIfFalse(message.getChat().getType().equals("private"));
        ErrorDescriptions.NO_INFO_ERROR.throwIfFalse(userService.checkUser(message.getFrom().getId()));

        Comparator<UserDto> dateComparator = Comparator.comparing(UserDto::getBirthdayDate);
        TreeSet<UserDto> recipients = new TreeSet<>(dateComparator);

        Set<Long> userChats = userService.getUser(message.getFrom().getId()).getUserChats();
        for (Long chat : userChats) {
            Set<Long> users = chatService.getChat(chat).getUsers();

            getReceivers(message, recipients, users);
        }
        Set<Long> friends = userService.getUser(message.getFrom().getId()).getFriends();
        getReceivers(message, recipients, friends);

        for (UserDto recipient : recipients) {
            postman.sendNotification(message.getFrom().getId(), recipient);
        }

        String answerText = recipients.isEmpty() ? Answers.SOON_NO_BIRTHDAYS : Answers.SOON_BIRTHDAYS;

        return SendMessage.builder()
                .chatId(message.getChatId())
                .text(answerText)
                .build();
    }

    /**
     * Поиск получателей
     *
     * @param recipients список получателей
     * @param users      список пользователей
     */
    private void getReceivers(Message message, TreeSet<UserDto> recipients, Set<Long> users) throws ApplicationException {
        for (Long user : users) {
            if (!Objects.equals(user, message.getFrom().getId())) {
                UserDto userDto = userService.getUser(user);
                LocalDate birthday = userDto.getBirthdayDate().withYear(LocalDate.now().getYear());

                if (birthday.isBefore(LocalDate.now().plusMonths(1)) &&
                        birthday.isAfter(LocalDate.now().minusDays(1))) {
                    recipients.add(userDto);
                }
            }
        }
    }

    @Override
    public Command getSupportedCommand() {
        return Command.SOON;
    }

}
