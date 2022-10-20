package com.example.happybirthdaybot.error;

import com.example.happybirthdaybot.error.model.ApplicationError;
import lombok.Getter;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Исключение приложения.
 *
 * @author Egor Mitrofanov.
 */
@Getter
public class ApplicationException extends TelegramApiException {

    /**
     * {@link ApplicationError}.
     */
    private final ApplicationError error;

    public ApplicationException(ApplicationError error) {
        super(error.getMessage());
        this.error = error;
    }

    public ApplicationException(ApplicationError error, Throwable throwable) {
        super(error.getMessage(), throwable);
        this.error = error;
    }

}
