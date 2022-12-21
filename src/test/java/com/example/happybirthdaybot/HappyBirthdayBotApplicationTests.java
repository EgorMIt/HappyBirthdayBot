package com.example.happybirthdaybot;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@ActiveProfiles("test")
class HappyBirthdayBotApplicationTests {

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

}
