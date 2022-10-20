package com.example.happybirthdaybot.dto;

import com.example.happybirthdaybot.domain.entity.NotificationLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

/**
 * Модель пользователя.
 *
 * @author Egor Mitrofanov.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class UserDto implements Comparable<UserDto> {

    /**
     * Идентификатор чата.
     */
    private Long userId;

    /**
     * Тег пользователя.
     */
    private String userTag;

    /**
     * Имя пользователя.
     */
    private String userName;

    /**
     * Фамилия пользователя.
     */
    private String userSurname;

    /**
     * День рождения.
     */
    private LocalDate birthdayDate;

    /**
     * Флаг заполненности даты.
     */
    private Boolean isRegistered;

    /**
     * Флаг изменения wishlist-а.
     */
    private Boolean isUpdating;

    /**
     * Настройка уведомлений.
     */
    private NotificationLevel notificationLevel;

    /**
     * Чаты пользователя.
     */
    private Set<Long> userChats;

    /**
     * Друзья пользователя.
     */
    private Set<Long> friends;

    /**
     * Wishlist пользователя.
     */
    private Set<String> wishlist;

    @Override
    public int compareTo(UserDto o) {
        return birthdayDate.compareTo(o.getBirthdayDate());
    }

}

