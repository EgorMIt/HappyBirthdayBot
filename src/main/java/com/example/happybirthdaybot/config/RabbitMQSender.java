package com.example.happybirthdaybot.config;

import com.example.happybirthdaybot.dto.NotificationDto;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Отправка сообщения RabbitMQ.
 *
 * @author Egor Mitrofanov.
 */
@Service
public class RabbitMQSender {

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Autowired
    private Queue queue;

    public void send(NotificationDto notificationDto) {
        rabbitTemplate.convertAndSend(queue.getName(), notificationDto);
    }

}