package com.example.happybirthdaybot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

/**
 * Модель напоминания.
 *
 * @author Egor Mitrofanov.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class NotificationDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -1138446817700416884L;

    /**
     * Идентификатор чата.
     */
    private Long userId;

    /**
     * Тег пользователя, у которого день рождения.
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
     * День рождения пользователя.
     */
    private String birthdayDate;

    /**
     * Wishlist пользователя.
     */
    private Set<String> wishlist;

}

