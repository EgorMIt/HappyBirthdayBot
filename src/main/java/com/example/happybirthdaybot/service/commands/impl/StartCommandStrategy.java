package com.example.happybirthdaybot.service.commands.impl;

import com.example.happybirthdaybot.common.Answers;
import com.example.happybirthdaybot.common.Command;
import com.example.happybirthdaybot.dto.ChatDto;
import com.example.happybirthdaybot.dto.UserDto;
import com.example.happybirthdaybot.error.ApplicationException;
import com.example.happybirthdaybot.service.commands.CommandStrategy;
import com.example.happybirthdaybot.service.data.ChatService;
import com.example.happybirthdaybot.service.data.MapStructMapper;
import com.example.happybirthdaybot.service.data.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Стратегия команды /start
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StartCommandStrategy implements CommandStrategy {

    /**
     * {@link UserService}.
     */
    private final UserService userService;

    /**
     * {@link ChatService}.
     */
    private final ChatService chatService;

    /**
     * {@link MapStructMapper}.
     */
    private final MapStructMapper mapper;

    @Override
    public SendMessage invokeCommand(Message message) throws ApplicationException {
        log.info("invoke command START: ({}, {})", message.getChatId(), message.getFrom().getUserName());
        String answerText;
        if (message.getChat().getType().equals("private")) {
            if (!userService.checkUser(message.getFrom().getId())) {
                UserDto userDto = mapper.mapToUserDto(message.getFrom());
                userService.createUser(userDto);
                answerText = Answers.START;
            } else if (userService.getUser(message.getFrom().getId()).getIsRegistered()) {
                answerText = Answers.FULLY_REGISTERED;
            } else {
                answerText = Answers.WAITING_FOR_DATE;
            }
        } else {
            answerText = Answers.START_IN_CHAT;
            if (chatService.checkChat(message.getChatId())) {
                answerText += chatService.getChat(message.getChatId()).getChatCode();
            } else {
                ChatDto chatDto = new ChatDto();
                chatDto.setChatId(message.getChatId());
                chatDto.setChatCode(chatService.generateChatCode());
                chatService.createChat(chatDto);

                answerText += chatDto.getChatCode();
            }
        }
        return SendMessage.builder()
                .chatId(message.getChatId())
                .text(answerText)
                .build();
    }

    @Override
    public Command getSupportedCommand() {
        return Command.START;
    }

}
