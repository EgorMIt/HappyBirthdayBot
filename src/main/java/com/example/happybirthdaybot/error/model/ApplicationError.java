package com.example.happybirthdaybot.error.model;

import lombok.Data;

/**
 * Ошибка приложения.
 *
 * @author Egor Mitrofanov.
 */
@Data
public class ApplicationError {

    /**
     * Сообщение пользователю.
     */
    private String responseText;

    /**
     * Краткое описание ошибки.
     */
    private String message;

    /**
     * Создание ошибки.
     *
     * @param responseText Сообщение пользователю.
     * @param message      Краткое описание ошибки.
     */
    public ApplicationError(String responseText, String message) {
        this.responseText = responseText;
        this.message = message;
    }

}
