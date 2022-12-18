package com.example.happybirthdaybot.bot;

import com.example.happybirthdaybot.common.Answers;
import com.example.happybirthdaybot.dto.NotificationDto;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Формирование сообщений.
 *
 * @author Egor Mitrofanov.
 */
@Slf4j
@Component
@RabbitListener(queues = "rabbitmq.queue", id = "listener")
public class MessageExecutor {

    /**
     * {@link Bot}
     */
    private final @Lazy Bot bot;

    public MessageExecutor(@Lazy Bot bot) {
        this.bot = bot;
    }

    /**
     * Отправка сообщения.
     */
    @SneakyThrows
    public Message sendDefaultMessage(String text, Message message) {
        if (!ObjectUtils.isEmpty(text)) {
            return bot.execute(SendMessage.builder()
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
            bot.execute(sendMessage);
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
        bot.execute(deleteMessage);

        if (!ObjectUtils.isEmpty(sendMessage) && !ObjectUtils.isEmpty(sendMessage.getText())) {
            bot.execute(sendMessage);
        }
    }

    /**
     * Изменение сообщения.
     */
    @SneakyThrows
    public void editMessage(EditMessageText editMessageText) {
        if (!ObjectUtils.isEmpty(editMessageText)) {
            bot.execute(editMessageText);
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
}
