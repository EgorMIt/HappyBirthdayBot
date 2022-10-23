package com.example.happybirthdaybot.bot;

import com.example.happybirthdaybot.common.Answers;
import com.example.happybirthdaybot.common.Command;
import com.example.happybirthdaybot.config.BotConfig;
import com.example.happybirthdaybot.controllers.ActionController;
import com.example.happybirthdaybot.controllers.CommandController;
import com.example.happybirthdaybot.dto.NotificationDto;
import com.example.happybirthdaybot.error.ApplicationException;
import com.example.happybirthdaybot.utils.MessageParser;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Реализация бота.
 *
 * @author Egor Mitrofanov.
 */
@Slf4j
@Component
@RabbitListener(queues = "rabbitmq.queue", id = "listener")
@RequiredArgsConstructor
public class Bot extends TelegramLongPollingBot {

    /**
     * {@link MessageParser}.
     */
    private final MessageParser messageParser;

    /**
     * {@link CommandController}.
     */
    private final CommandController commandController;

    /**
     * {@link ActionController}.
     */
    private final ActionController actionController;

    /**
     * {@link BotConfig}.
     */
    private final BotConfig botConfig;

    /**
     * Основной обработчик событий.
     */
    @Override
    public void onUpdateReceived(Update update) {
        log.info("new update received: ({})", update.toString());
        try {
            //Обычные сообщения и команды
            if (update.hasMessage() && update.getMessage().hasText()) {

                String text = update.getMessage().getText();
                if (text.charAt(0) == '/') {
                    Message wait = sendDefaultMessage(Answers.WAITING, update.getMessage());
                    Command command = messageParser.checkForCommand(text);
                    switch (command) {
                        case START ->
                                sendDefaultMessageAndDeletePrevious(commandController.startCommand(update.getMessage()), wait);
                        case CLEAN ->
                                sendDefaultMessageAndDeletePrevious(commandController.cleanCommand(update.getMessage()), wait);
                        case INFO ->
                                sendDefaultMessageAndDeletePrevious(commandController.infoCommand(update.getMessage()), wait);
                        case SOON ->
                                sendDefaultMessageAndDeletePrevious(commandController.soonCommand(update.getMessage()), wait);
                        case NOTIFICATIONS ->
                                sendDefaultMessageAndDeletePrevious(commandController.notificationsCommand(update.getMessage()), wait);
                        case WISHLIST ->
                                sendDefaultMessageAndDeletePrevious(commandController.wishlistCommand(update.getMessage()), wait);
                        case EMPTY ->
                                sendDefaultMessageAndDeletePrevious(commandController.emptyCommand(update.getMessage()), wait);
                        case FRIEND ->
                                sendDefaultMessageAndDeletePrevious(commandController.friendCommand(update.getMessage()), wait);
                        case ADMIN ->
                                sendDefaultMessageAndDeletePrevious(commandController.adminCommand(update.getMessage()), wait);
                    }
                } else {
                    if (messageParser.hasDate(text)) {
                        sendDefaultMessage(actionController.updateDate(update.getMessage()));
                    } else if (messageParser.hasCode(text)) {
                        sendDefaultMessage(actionController.joinChatByCode(update.getMessage()));
                    } else if (messageParser.hasUserTag(text)) {
                        sendDefaultMessage(actionController.friendAdded(update.getMessage()));
                    } else {
                        Message wait = sendDefaultMessage(Answers.WAITING, update.getMessage());
                        sendDefaultMessageAndDeletePrevious(actionController.updateWishlist(update.getMessage()), wait);
                    }
                }
            }
            //Действия, не имеющие текста (Вход, выход)
            else if (update.hasMessage() && !update.getMessage().hasText()) {
                if (update.getMessage().getLeftChatMember() != null &&
                        update.getMessage().getLeftChatMember().getIsBot() &&
                        update.getMessage().getLeftChatMember().getFirstName().equals(getBotUsername())) {
                    actionController.botLeftChat(update);
                }
            }
            //Действия, не имеющие сообщения (Блок бота пользователем)
            else if (update.getMyChatMember() != null &&
                    update.getMyChatMember().getOldChatMember() != null &&
                    update.getMyChatMember().getOldChatMember().getUser().getIsBot() &&
                    update.getMyChatMember().getOldChatMember().getUser().getFirstName().equals(getBotUsername())) {
                actionController.userBlockedBot(update);
            }
            //Действия с callBack (Кнопки)
            else if (update.hasCallbackQuery()) {
                String callBackData = update.getCallbackQuery().getData();

                if (messageParser.checkForNotification(callBackData)) {
                    editMessage(actionController.setNotificationLevel(update, callBackData));
                }
            }
        } catch (ApplicationException e) {
            sendDefaultMessage(e.getError().getResponseText(), update.getMessage());
        }
    }

    /**
     * Отправка сообщения.
     */
    @SneakyThrows
    public Message sendDefaultMessage(String text, Message message) {
        if (!ObjectUtils.isEmpty(text)) {
            return execute(SendMessage.builder()
                    .chatId(message.getChatId())
                    .text(text)
                    .build());
        }
        return null;
    }

    /**
     * Отправка сообщения.
     */
    @SneakyThrows
    public void sendDefaultMessage(SendMessage sendMessage) {
        if (!ObjectUtils.isEmpty(sendMessage) && !ObjectUtils.isEmpty(sendMessage.getText())) {
            execute(sendMessage);
        }
    }

    /**
     * Отправка сообщения с ожиданием.
     */
    @SneakyThrows
    public void sendDefaultMessageAndDeletePrevious(SendMessage sendMessage, Message previous) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(previous.getChatId());
        deleteMessage.setMessageId(previous.getMessageId());
        execute(deleteMessage);

        if (!ObjectUtils.isEmpty(sendMessage) && !ObjectUtils.isEmpty(sendMessage.getText())) {
            execute(sendMessage);
        }
    }

    /**
     * Изменение сообщения.
     */
    @SneakyThrows
    public void editMessage(EditMessageText editMessageText) {
        if (!ObjectUtils.isEmpty(editMessageText)) {
            execute(editMessageText);
        }
    }

    /**
     * Обработчик сообщений.
     */
    @RabbitHandler
    public void receiver(NotificationDto notificationDto) {
        log.info("received message: ({})", notificationDto.toString());

        StringBuilder answerText = new StringBuilder();
        answerText.append(Answers.BIRTHDAY_NOTIFICATION_1);
        if (!ObjectUtils.isEmpty(notificationDto.getUserTag())) {
            answerText.append("@")
                    .append(notificationDto.getUserTag());
        } else {
            answerText.append(Answers.BIRTHDAY_NOTIFICATION_1)
                    .append(notificationDto.getUserName())
                    .append(" ")
                    .append(notificationDto.getUserSurname());
        }
        answerText.append(Answers.BIRTHDAY_NOTIFICATION_2)
                .append(notificationDto.getBirthdayDate());

        if (!notificationDto.getWishlist().isEmpty()) {
            answerText.append("\n\nВот список идей для подарков:");
            for (String wish : notificationDto.getWishlist()) {
                answerText.append("\n - ")
                        .append(wish);
            }
        }
        sendDefaultMessage(SendMessage.builder()
                .chatId(notificationDto.getUserId())
                .text(answerText.toString())
                .build());
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }

}
