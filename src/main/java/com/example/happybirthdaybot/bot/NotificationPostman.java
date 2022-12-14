package com.example.happybirthdaybot.bot;

import com.example.happybirthdaybot.config.RabbitMQSender;
import com.example.happybirthdaybot.domain.entity.NotificationLevel;
import com.example.happybirthdaybot.dto.NotificationDto;
import com.example.happybirthdaybot.dto.UserDto;
import com.example.happybirthdaybot.error.ApplicationException;
import com.example.happybirthdaybot.service.data.ChatService;
import com.example.happybirthdaybot.service.data.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Отправка напоминаний.
 *
 * @author Egor Mitrofanov.
 */
@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class NotificationPostman {

    /**
     * {@link UserService}.
     */
    private final UserService userService;

    /**
     * {@link ChatService}.
     */
    private final ChatService chatService;

    /**
     * {@link RabbitMQSender}.
     */
    private final RabbitMQSender rabbitMQSender;

    /**
     * Ежедневная проверка дат.
     */
    @Scheduled(cron = "0 00 23 * * ?")
    public void CheckBirthdays() throws ApplicationException {
        log.info("start checking birthdays");

        sendNotificationsByLevel(NotificationLevel.DAY);
        sendNotificationsByLevel(NotificationLevel.THREE_DAYS);
        sendNotificationsByLevel(NotificationLevel.WEEK);
    }

    /**
     * Поиск получателей и отправка напоминаний.
     */
    public void sendNotificationsByLevel(NotificationLevel notificationLevel) throws ApplicationException {
        LocalDate now = LocalDate.now();
        List<UserDto> users = userService.findUsersByBirthday(now.plusDays(notificationLevel.getNumberOfDays()));
        for (UserDto user : users) {
            Set<Long> receivers = new HashSet<>();

            //Поиск по групповым чатам
            Set<Long> userChats = user.getUserChats();
            for (Long chat : userChats) {
                Set<Long> chatUsers = chatService.getChat(chat).getUsers();

                for (Long chatUser : chatUsers) {
                    if (!Objects.equals(chatUser, user.getUserId())) {
                        NotificationLevel currentLevel = userService.getUser(chatUser).getNotificationLevel();
                        if (currentLevel == notificationLevel && currentLevel != NotificationLevel.NEVER) {
                            receivers.add(chatUser);
                        }
                    }
                }
            }

            //Поиск по спискам друзей
            List<UserDto> userFriends = userService.findUsersFriendWith(user.getUserId());
            for (UserDto friend : userFriends) {
                if (!Objects.equals(friend.getUserId(), user.getUserId())) {
                    NotificationLevel currentLevel = userService.getUser(friend.getUserId()).getNotificationLevel();
                    if (currentLevel == notificationLevel && currentLevel != NotificationLevel.NEVER) {
                        receivers.add(friend.getUserId());
                    }
                }
            }

            for (Long receiver : receivers) {
                sendNotification(receiver, user);
            }
        }
    }

    /**
     * Отправка напоминания.
     */
    public void sendNotification(Long receiver, UserDto user) {
        log.info("send message to: ({})", receiver);

        rabbitMQSender.send(NotificationDto.of(receiver,
                user.getUserTag(),
                user.getUserName(),
                user.getUserSurname(),
                user.getBirthdayDate().withYear(LocalDate.now().getYear())
                        .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)),
                user.getWishlist()));
    }
}
