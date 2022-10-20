package com.example.happybirthdaybot.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Конфигурация бота.
 *
 * @author Egor Mitrofanov.
 */
@Getter
@Configuration
@PropertySource("classpath:config/bot-configuration.properties")
public class BotConfig {

    @Value("${bot.name}")
    private String botUsername;

    @Value("${bot.token}")
    private String botToken;

    @Value("${bot.chatCodeSize}")
    private Integer chatCodeSize;

}
