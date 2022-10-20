package com.example.happybirthdaybot.utils;


import com.example.happybirthdaybot.domain.entity.ChatEntity;
import com.example.happybirthdaybot.domain.entity.UserEntity;
import com.example.happybirthdaybot.domain.entity.WishEntity;
import com.example.happybirthdaybot.dto.ChatDto;
import com.example.happybirthdaybot.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.stream.Collectors;

/**
 * Маппер моделей.
 *
 * @author Egor Mitrofanov.
 */
@Component
@RequiredArgsConstructor
public class ModelMapper {

    /**
     * Маппер сущности {@link User} в модель {@link UserDto}.
     *
     * @param user сущность.
     * @return модель {@link UserDto}.
     */
    public UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setUserId(user.getId());
        userDto.setUserTag(user.getUserName());
        userDto.setUserName(user.getFirstName());
        userDto.setUserSurname(user.getLastName());
        return userDto;
    }

    /**
     * Маппер сущности {@link UserEntity} в модель {@link UserDto}.
     *
     * @param userEntity сущность.
     * @return модель {@link UserDto}.
     */
    public UserDto mapToUserDto(UserEntity userEntity) {
        UserDto userDto = new UserDto();
        userDto.setUserId(userEntity.getUserId());
        userDto.setUserTag(userEntity.getUserTag());
        userDto.setUserName(userEntity.getUserName());
        userDto.setUserSurname(userEntity.getUserSurname());
        userDto.setBirthdayDate(userEntity.getBirthdayDate());
        userDto.setIsRegistered(userEntity.getIsRegistered());
        userDto.setIsUpdating(userEntity.getIsUpdating());
        userDto.setNotificationLevel(userEntity.getNotificationLevel());
        userDto.setUserChats(userEntity.getUserChat()
                .stream()
                .map((item) -> item.getChat().getChatId())
                .collect(Collectors.toSet()));
        userDto.setFriends(userEntity.getFriends()
                .stream()
                .map(item -> item.getFriend().getUserId())
                .collect(Collectors.toSet()));
        userDto.setWishlist(userEntity.getWishlist()
                .stream()
                .map(WishEntity::getWish)
                .collect(Collectors.toSet()));
        return userDto;
    }

    /**
     * Маппер сущности {@link ChatEntity} в модель {@link ChatDto}.
     *
     * @param chatEntity сущность.
     * @return модель {@link ChatDto}.
     */
    public ChatDto mapToChatDto(ChatEntity chatEntity) {
        ChatDto chatDto = new ChatDto();
        chatDto.setChatId(chatEntity.getChatId());
        chatDto.setChatCode(chatEntity.getChatCode());
        chatDto.setUsers(chatEntity.getUserChat()
                .stream()
                .map((item) -> item.getUser().getUserId())
                .collect(Collectors.toSet()));
        return chatDto;
    }


}
