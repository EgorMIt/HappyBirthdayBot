package com.example.happybirthdaybot.controllers;

import com.example.happybirthdaybot.common.Answers;
import com.example.happybirthdaybot.domain.entity.NotificationLevel;
import com.example.happybirthdaybot.dto.UserDto;
import com.example.happybirthdaybot.error.ApplicationException;
import com.example.happybirthdaybot.error.ErrorDescriptions;
import com.example.happybirthdaybot.service.ChatService;
import com.example.happybirthdaybot.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Обработчик запросов действий, вызванных не командой.
 *
 * @author Egor Mitrofanov.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ActionController {

    /**
     * {@link UserService}.
     */
    private final UserService userService;

    /**
     * {@link ChatService}.
     */
    private final ChatService chatService;

    /**
     * Обработчик команды добавления даты.
     *
     * @param message входящее сообщение.
     * @return {@link SendMessage} ответное сообщение.
     */
    public SendMessage updateDate(Message message) throws ApplicationException {
        log.info("invoke updateDate: ({}, {})", message.getChatId(), message.getFrom().getUserName());

        ErrorDescriptions.NO_INFO_ERROR.throwIfFalse(userService.checkUser(message.getFrom().getId()));

        UserDto userDto = userService.getUser(message.getChatId());
        if (!userDto.getIsRegistered()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            LocalDate birthdayDate = LocalDate.parse(message.getText().trim(), formatter);

            userDto.setBirthdayDate(birthdayDate);
            userDto.setIsRegistered(true);
            userService.updateUser(userDto);

            return SendMessage.builder()
                    .chatId(message.getChatId())
                    .text(Answers.DATE_FILLED)
                    .build();
        } else {
            return null;
        }
    }

    /**
     * Обработчик кода присоединения к чату.
     *
     * @param message входящее сообщение.
     * @return {@link SendMessage} ответное сообщение.
     */
    public SendMessage joinChatByCode(Message message) throws ApplicationException {
        log.info("invoke joinChatByCode: ({}, {})", message.getChatId(), message.getFrom().getUserName());

        ErrorDescriptions.NO_INFO_ERROR.throwIfFalse(userService.checkUser(message.getFrom().getId()));
        ErrorDescriptions.JOIN_ERROR.throwIfFalse(chatService.checkCode(Integer.valueOf(message.getText().trim())));

        String answerText = Answers.JOINED_CHAT;

        userService.addUserToChat(message.getFrom().getId(), Integer.valueOf(message.getText().trim()));

        return SendMessage.builder()
                .chatId(message.getChatId())
                .text(answerText)
                .build();
    }

    /**
     * Настройка Notification Level.
     *
     * @param update       новый update.
     * @param callBackData данные для обновления.
     * @return {@link EditMessageText} ответное сообщение.
     */
    public EditMessageText setNotificationLevel(Update update, String callBackData) throws ApplicationException {
        log.info("invoke setNotificationLevel: ({}, {})", update.getCallbackQuery().getFrom().getId(), callBackData);

        Long userId = update.getCallbackQuery().getFrom().getId();
        ErrorDescriptions.NO_INFO_ERROR.throwIfFalse(userService.checkUser(userId));

        NotificationLevel newLevel = NotificationLevel.valueOf(callBackData);

        String answerText = Answers.NOTIFICATION_CHANGED;
        UserDto userDto = userService.getUser(userId);
        userDto.setNotificationLevel(newLevel);

        userService.updateUser(userDto);

        EditMessageText message = new EditMessageText();
        message.setChatId(userId);
        message.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        message.setText(answerText);
        return message;
    }

    /**
     * Обработчик удаления бота из чата.
     *
     * @param update действие.
     */
    public void botLeftChat(Update update) throws ApplicationException {
        log.info("invoke botLeftChat: ({})", update.getMessage().getChatId());

        if (chatService.checkChat(update.getMessage().getChatId())) {
            chatService.deleteChat(update.getMessage().getChatId());
        }
    }

    /**
     * Обработчик блокировки бота пользователем.
     *
     * @param update действие.
     */
    public void userBlockedBot(Update update) throws ApplicationException {
        log.info("invoke userBlockedBot: ({})", update.getMyChatMember().getChat().getId());

        if (userService.checkUser(update.getMyChatMember().getChat().getId())) {
            userService.deleteUser(update.getMyChatMember().getChat().getId());
        }
    }

    /**
     * Обработчик добавления wishlist-а.
     *
     * @param message входящее сообщение.
     * @return {@link SendMessage} ответное сообщение.
     */
    public SendMessage updateWishlist(Message message) throws ApplicationException {
        log.info("invoke updateWishlist: ({}, {})", message.getChatId(), message.getFrom().getUserName());

        ErrorDescriptions.NO_INFO_ERROR.throwIfFalse(userService.checkUser(message.getFrom().getId()));

        UserDto userDto = userService.getUser(message.getFrom().getId());
        if (userDto.getIsUpdating()) {
            String[] arr = message.getText().split("\n");
            Set<String> wishlist = new LinkedHashSet<>(Arrays.asList(arr));

            userService.addWishlistToUser(message.getFrom().getId(), wishlist);

            userDto.setIsUpdating(false);
            userService.updateUser(userDto);
            return SendMessage.builder()
                    .chatId(message.getChatId())
                    .text(Answers.WISHLIST_ADDED)
                    .build();
        } else return null;
    }

    /**
     * Обработчик добавления пользователя в друзья.
     *
     * @param message входящее сообщение.
     * @return {@link SendMessage} ответное сообщение.
     */
    public SendMessage friendAdded(Message message) throws ApplicationException {
        log.info("invoke friendAdded: ({}, {})", message.getChatId(), message.getFrom().getUserName());

        ErrorDescriptions.NO_INFO_ERROR.throwIfFalse(userService.checkUser(message.getFrom().getId()));

        String userTag = message.getText().trim().split("@")[1];
        String answerText;
        if (userService.checkUserByTag(userTag)) {
            userService.addFriend(message.getFrom().getId(), userTag);
            answerText = Answers.FRIEND_ADDED;
        } else answerText = Answers.FRIEND_NOT_FOUND;
        return SendMessage.builder()
                .chatId(message.getChatId())
                .text(answerText)
                .build();
    }

}
