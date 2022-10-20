package com.example.happybirthdaybot.controllers;

import com.example.happybirthdaybot.bot.NotificationPostman;
import com.example.happybirthdaybot.common.Answers;
import com.example.happybirthdaybot.domain.entity.NotificationLevel;
import com.example.happybirthdaybot.dto.ChatDto;
import com.example.happybirthdaybot.dto.UserDto;
import com.example.happybirthdaybot.error.ApplicationException;
import com.example.happybirthdaybot.error.ErrorDescriptions;
import com.example.happybirthdaybot.service.ChatService;
import com.example.happybirthdaybot.service.UserService;
import com.example.happybirthdaybot.utils.KeyboardBuilder;
import com.example.happybirthdaybot.utils.ModelMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Обработчик запросов к {@link UserService}.
 *
 * @author Egor Mitrofanov.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CommandController {

    /**
     * {@link UserService}.
     */
    private final UserService userService;

    /**
     * {@link ChatService}.
     */
    private final ChatService chatService;

    /**
     * {@link ModelMapper}.
     */
    private final ModelMapper modelMapper;

    /**
     * {@link KeyboardBuilder}.
     */
    private final KeyboardBuilder keyboardBuilder;

    /**
     * {@link NotificationPostman}.
     */
    private final NotificationPostman postman;

    /**
     * Обработчик команды /start.
     *
     * @param message входящее сообщение.
     * @return {@link SendMessage} ответное сообщение.
     */
    public SendMessage startCommand(Message message) throws ApplicationException {
        log.info("invoke command START: ({}, {})", message.getChatId(), message.getFrom().getUserName());
        String answerText;
        if (message.getChat().getType().equals("private")) {
            if (!userService.checkUser(message.getFrom().getId())) {
                UserDto userDto = modelMapper.mapToUserDto(message.getFrom());
                userService.createUser(userDto);
                answerText = Answers.START;
            } else if (userService.getUser(message.getFrom().getId()).getIsRegistered()) {
                answerText = Answers.FULLY_REGISTERED;
            } else {
                answerText = Answers.WAITING_FOR_DATE;
            }
        } else {
            answerText = Answers.START_IN_CHAT;
            if (chatService.checkChat(message.getChatId())) {
                answerText += chatService.getChat(message.getChatId()).getChatCode();
            } else {
                ChatDto chatDto = new ChatDto();
                chatDto.setChatId(message.getChatId());
                chatDto.setChatCode(chatService.generateChatCode());
                chatService.createChat(chatDto);

                answerText += chatDto.getChatCode();
            }
        }
        return SendMessage.builder()
                .chatId(message.getChatId())
                .text(answerText)
                .build();
    }

    /**
     * Обработчик команды /clean.
     *
     * @param message входящее сообщение.
     * @return {@link SendMessage} ответное сообщение.
     */
    public SendMessage cleanCommand(Message message) throws ApplicationException {
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

    /**
     * Обработчик команды /info.
     *
     * @param message входящее сообщение.
     * @return {@link SendMessage} ответное сообщение.
     */
    public SendMessage infoCommand(Message message) throws ApplicationException {
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

    /**
     * Обработчик команды /notifications.
     *
     * @param message входящее сообщение.
     * @return {@link SendMessage} ответное сообщение.
     */
    public SendMessage notificationsCommand(Message message) throws ApplicationException {
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

    /**
     * Обработчик команды /soon.
     *
     * @param message входящее сообщение.
     * @return {@link SendMessage} ответное сообщение.
     */
    public SendMessage soonCommand(Message message) throws ApplicationException {
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

        return SendMessage.builder()
                .chatId(message.getChatId())
                .text(Answers.SOON_BIRTHDAYS)
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
                LocalDate birthday = userDto.getBirthdayDate();

                if (birthday.isBefore(LocalDate.now().plusMonths(1))) {
                    recipients.add(userDto);
                }
            }
        }
    }

    /**
     * Обработчик команды /wishlist.
     *
     * @param message входящее сообщение.
     * @return {@link SendMessage} ответное сообщение.
     */
    public SendMessage wishlistCommand(Message message) throws ApplicationException {
        log.info("invoke command WISHLIST: ({}, {})", message.getChatId(), message.getFrom().getUserName());

        ErrorDescriptions.INVALID_COMMAND_IN_CHAT.throwIfFalse(message.getChat().getType().equals("private"));
        ErrorDescriptions.NO_INFO_ERROR.throwIfFalse(userService.checkUser(message.getFrom().getId()));

        UserDto userDto = userService.getUser(message.getChatId());
        userDto.setIsUpdating(true);
        userService.updateUser(userDto);

        return SendMessage.builder()
                .chatId(message.getChatId())
                .text(Answers.WISHLIST)
                .build();
    }

    /**
     * Обработчик команды /empty.
     *
     * @param message входящее сообщение.
     * @return {@link SendMessage} ответное сообщение.
     */
    public SendMessage emptyCommand(Message message) throws ApplicationException {
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

    /**
     * Обработчик команды /friend.
     *
     * @param message входящее сообщение.
     * @return {@link SendMessage} ответное сообщение.
     */
    public SendMessage friendCommand(Message message) throws ApplicationException {
        log.info("invoke command FRIEND: ({}, {})", message.getChatId(), message.getFrom().getUserName());

        ErrorDescriptions.INVALID_COMMAND_IN_CHAT.throwIfFalse(message.getChat().getType().equals("private"));
        ErrorDescriptions.NO_INFO_ERROR.throwIfFalse(userService.checkUser(message.getFrom().getId()));


        return SendMessage.builder()
                .chatId(message.getChatId())
                .text(Answers.FRIEND_COMMAND)
                .build();
    }

}
