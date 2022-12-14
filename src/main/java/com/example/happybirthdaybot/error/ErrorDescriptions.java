package com.example.happybirthdaybot.error;

import com.example.happybirthdaybot.common.Answers;
import com.example.happybirthdaybot.error.model.ApplicationError;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.ObjectUtils;

/**
 * Ошибки с комментариями.
 *
 * @author Egor Mitrofanov.
 */
@Getter
@RequiredArgsConstructor
public enum ErrorDescriptions {

    INVALID_COMMAND(Answers.INVALID_COMMAND, "Invalid command"),
    APPLICATION_ERROR(Answers.APPLICATION_ERROR, "Invalid input data"),
    JOIN_ERROR(Answers.JOINED_FAILED, "Invalid join data"),
    NO_INFO_ERROR(Answers.NO_INFO, "User not registered"),
    INVALID_COMMAND_IN_CHAT(Answers.INVALID_COMMAND_IN_CHAT, "Invalid chat command"),
    HANDLER_NOT_FOUND(Answers.APPLICATION_ERROR, "Handler not found");

    /**
     * Сообщение пользователю.
     */
    private final String responseText;

    /**
     * Краткое описание ошибки.
     */
    private final String message;

    /**
     * Метод выбрасывает исключение приложения.
     *
     * @throws ApplicationException исключение приложения
     */
    public void throwException() throws ApplicationException {
        throw exception();
    }

    /**
     * Метод выбрасывает исключение если объект == null.
     *
     * @param obj объект для проверки
     */
    public void throwIfNullOrEmpty(Object obj) throws ApplicationException {
        if (ObjectUtils.isEmpty(obj)) {
            throw exception();
        }
    }

    /**
     * Метод выбрасывает исключение если условие истинно.
     *
     * @param condition условие для проверки
     */
    public void throwIfTrue(Boolean condition) throws ApplicationException {
        if (condition) {
            throw exception();
        }
    }

    /**
     * Метод выбрасывает исключение если условие ложно.
     *
     * @param condition условие для проверки
     */
    public void throwIfFalse(Boolean condition) throws ApplicationException {
        if (!condition) {
            throw exception();
        }
    }

    public ApplicationError applicationError() {
        return new ApplicationError(this.responseText, this.message);
    }


    public ApplicationException exception() {
        return new ApplicationException(applicationError());
    }
}
