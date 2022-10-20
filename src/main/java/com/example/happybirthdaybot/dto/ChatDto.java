package com.example.happybirthdaybot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Модель чата.
 *
 * @author Egor Mitrofanov.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class ChatDto {

    /**
     * Идентификатор чата.
     */
    private Long chatId;

    /**
     * Тег пользователя.
     */
    private Integer chatCode;

    /**
     * Пользователи.
     */
    private Set<Long> users;

}
