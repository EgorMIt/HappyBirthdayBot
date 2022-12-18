package com.example.happybirthdaybot.bot;

import com.example.happybirthdaybot.common.Command;
import com.example.happybirthdaybot.config.BotConfig;
import com.example.happybirthdaybot.error.ApplicationException;
import com.example.happybirthdaybot.service.actions.ActionService;
import com.example.happybirthdaybot.service.commands.CommandService;
import com.example.happybirthdaybot.utils.MessageParser;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;

/**
 * Реализация бота.
 *
 * @author Egor Mitrofanov.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class Bot extends TelegramLongPollingBot {

    /**
     * {@link MessageParser}.
     */
    private final MessageParser messageParser;

    /**
     * {@link ActionService}.
     */
    private final ActionService actionService;

    /**
     * {@link CommandService}
     */
    private final CommandService commandService;

    /**
     * {@link BotConfig}.
     */
    private final BotConfig botConfig;

    /**
     * Основной обработчик событий.
     */
    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        log.info("new update received: ({})", update.toString());
        try {
            //Обычные сообщения и команды
            if (update.hasMessage() && update.getMessage().hasText()) {
                String text = update.getMessage().getText();

                if (messageParser.hasCommand(text)) {
                    Command command = messageParser.checkForCommand(text);
                    commandService.invokeCommand(command, update.getMessage());
                } else {
                    if (messageParser.hasDate(text)) {
                        actionService.updateDate(update.getMessage());
                    } else if (messageParser.hasCode(text)) {
                        actionService.joinChatByCode(update.getMessage());
                    } else if (messageParser.hasUserTag(text)) {
                        actionService.friendAdded(update.getMessage());
                    } else {
                        actionService.updateWishlist(update.getMessage());
                    }
                }
            }
            //Действия, не имеющие текста (Вход, выход)
            else if (update.hasMessage() && !update.getMessage().hasText()) {
                if (update.getMessage().getLeftChatMember() != null &&
                        update.getMessage().getLeftChatMember().getIsBot() &&
                        update.getMessage().getLeftChatMember().getFirstName().equals(getBotUsername())) {
                    actionService.botLeftChat(update);
                }
            }
            //Действия, не имеющие сообщения (Блок бота пользователем)
            else if (update.getMyChatMember() != null &&
                    update.getMyChatMember().getOldChatMember() != null &&
                    update.getMyChatMember().getOldChatMember().getUser().getIsBot() &&
                    update.getMyChatMember().getOldChatMember().getUser().getFirstName().equals(getBotUsername())) {
                actionService.userBlockedBot(update);
            }
            //Действия с callBack (Кнопки)
            else if (update.hasCallbackQuery()) {
                String callBackData = update.getCallbackQuery().getData();

                if (messageParser.checkForNotification(callBackData)) {
                    actionService.setNotificationLevel(update, callBackData);
                }
            }
        } catch (ApplicationException e) {
            log.info("catch exception: ({})", e.getError().getMessage());

            execute(SendMessage.builder()
                    .chatId(update.getMessage().getChatId())
                    .text(e.getError().getResponseText())
                    .build());
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }

    @Override
    public <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method) throws TelegramApiException {
        return super.execute(method);
    }

}
