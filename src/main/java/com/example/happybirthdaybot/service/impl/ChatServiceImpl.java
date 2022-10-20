package com.example.happybirthdaybot.service.impl;

import com.example.happybirthdaybot.config.BotConfig;
import com.example.happybirthdaybot.domain.entity.ChatEntity;
import com.example.happybirthdaybot.domain.repository.ChatRepository;
import com.example.happybirthdaybot.dto.ChatDto;
import com.example.happybirthdaybot.error.ApplicationException;
import com.example.happybirthdaybot.error.ErrorDescriptions;
import com.example.happybirthdaybot.service.ChatService;
import com.example.happybirthdaybot.utils.ModelMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * Реализация сервиса работы с chat.
 *
 * @author Egor Mitrofanov.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    /**
     * {@link ChatRepository}.
     */
    private final ChatRepository chatRepository;

    /**
     * {@link ModelMapper}.
     */
    private final ModelMapper modelMapper;

    /**
     * {@link BotConfig}.
     */
    private final BotConfig botConfig;

    /**
     * Проверить существование чата в бд.
     *
     * @param chatId идентификатор чата.
     */
    @Override
    public Boolean checkChat(Long chatId) {
        return chatRepository.findChatEntityByChatId(chatId).isPresent();
    }

    /**
     * Проверить существование кода для чата в бд.
     *
     * @param chatCode идентификатор чата.
     */
    @Override
    public Boolean checkCode(Integer chatCode) {
        return chatRepository.findChatEntityByChatCode(chatCode).isPresent();
    }

    /**
     * Получение чата по chatId.
     *
     * @param chatId идентификатор чата.
     */
    @Override
    public ChatDto getChat(Long chatId) throws ApplicationException {
        ChatEntity chatEntity = chatRepository.findChatEntityByChatId(chatId).orElseThrow(ErrorDescriptions.APPLICATION_ERROR::exception);
        return modelMapper.mapToChatDto(chatEntity);
    }

    /**
     * Добавление чата в бд.
     *
     * @param chatDto модель.
     */
    @Override
    public void createChat(ChatDto chatDto) {
        ChatEntity chatEntity = new ChatEntity();
        chatEntity.setChatId(chatDto.getChatId());
        chatEntity.setChatCode(chatDto.getChatCode());

        chatRepository.save(chatEntity);
    }

    /**
     * Генерация нового кода для чата.
     *
     * @return 4-х значный код.
     */
    @Override
    public Integer generateChatCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < botConfig.getChatCodeSize(); i++) {
            int digit = new Random().nextInt(10);
            while (digit == 0 && i == 0)
                digit = new Random().nextInt(10);

            code.append(digit);
        }
        Integer newCode = Integer.parseInt(code.toString());
        if (checkCode(newCode)) {
            return generateChatCode();
        } else return newCode;
    }

    /**
     * Удалить информацию о чате.
     *
     * @param chatId идентификатор чата.
     */
    @Override
    public void deleteChat(Long chatId) throws ApplicationException {
        ChatEntity chatEntity = chatRepository.findChatEntityByChatId(chatId).orElseThrow(ErrorDescriptions.APPLICATION_ERROR::exception);
        chatRepository.delete(chatEntity);
    }

}
