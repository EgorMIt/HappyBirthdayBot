package com.example.happybirthdaybot.utils;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Конструктор кнопок в сообщениях.
 *
 * @author Egor Mitrofanov.
 */
@Component
public class KeyboardBuilder {

    /**
     * Создать список кнопок.
     *
     * @param buttons кнопки.
     */
    public InlineKeyboardMarkup getButtonList(Map<String, String> buttons) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        for (Map.Entry<String, String> entry : buttons.entrySet()) {
            List<InlineKeyboardButton> rowInline = new ArrayList<>();

            String key = entry.getKey();
            String value = entry.getValue();

            rowInline.add(getButton(key, value));
            rowsInline.add(rowInline);
        }
        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }

    /**
     * Создать новую кнопку.
     *
     * @param text     текст кнопки.
     * @param callBack возвратные данные.
     */
    public InlineKeyboardButton getButton(String text, String callBack) {
        InlineKeyboardButton newButton = new InlineKeyboardButton();
        newButton.setText(text);
        newButton.setCallbackData(callBack);

        return newButton;
    }

}
