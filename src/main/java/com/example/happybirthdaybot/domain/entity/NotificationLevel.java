package com.example.happybirthdaybot.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Список уровней уведомлений.
 *
 * @author Egor Mitrofanov.
 */
@Getter
@RequiredArgsConstructor
public enum NotificationLevel {
    DAY(1),
    THREE_DAYS(3),
    WEEK(7),
    NEVER(-1);

    /**
     * Количество дней.
     */
    private final Integer numberOfDays;

}
