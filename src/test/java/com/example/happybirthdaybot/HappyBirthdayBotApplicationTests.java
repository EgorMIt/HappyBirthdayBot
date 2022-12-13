package com.example.happybirthdaybot;

import com.example.happybirthdaybot.bot.Bot;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@ActiveProfiles("test")
class HappyBirthdayBotApplicationTests {

    @Autowired
    private Bot bot;

    @Test
    void contextLoads() {
    }

    @Test
    public void updateReceiveTest() {
        TelegramLongPollingBot bot = Mockito.mock(TelegramLongPollingBot.class);
        Mockito.doCallRealMethod().when(bot).onUpdatesReceived(any());
        Update update1 = new Update();
        update1.setUpdateId(1);
        bot.onUpdatesReceived(List.of(update1));
        Mockito.verify(bot).onUpdateReceived(update1);
    }

    @Test
    @Disabled
    public void userRegisterReceive() {
        Update update1 = new Update();
        update1.setUpdateId(1);
        Message message = new Message();
        message.setChat(new Chat(402975103L, "private"));
        User user = new User(402975103L, "Test", false);
        user.setUserName("test");
        message.setFrom(user);
        message.setText("/start");


        update1.setMessage(message);
        bot.onUpdatesReceived(List.of(update1));
        Mockito.verify(bot).onUpdateReceived(update1);
    }

}
