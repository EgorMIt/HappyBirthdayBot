package com.example.happybirthdaybot.service.data;

import com.example.happybirthdaybot.domain.entity.ChatEntity;
import com.example.happybirthdaybot.domain.entity.UserEntity;
import com.example.happybirthdaybot.dto.ChatDto;
import com.example.happybirthdaybot.dto.UserDto;
import org.mapstruct.Mapper;
import org.telegram.telegrambots.meta.api.objects.User;

/**
 * Маппер моделей.
 *
 * @author Egor Mitrofanov.
 */
@Mapper(componentModel = "spring")
public interface MapStructMapper {

    /**
     * Маппер сущности {@link User} в модель {@link UserDto}.
     *
     * @param user сущность.
     * @return модель {@link UserDto}.
     */
    UserDto mapToUserDto(User user);

    /**
     * Маппер сущности {@link UserEntity} в модель {@link UserDto}.
     *
     * @param userEntity сущность.
     * @return модель {@link UserDto}.
     */
    UserDto mapToUserDto(UserEntity userEntity);

    /**
     * Маппер сущности {@link ChatEntity} в модель {@link ChatDto}.
     *
     * @param chatEntity сущность.
     * @return модель {@link ChatDto}.
     */
    ChatDto mapToChatDto(ChatEntity chatEntity);

}
