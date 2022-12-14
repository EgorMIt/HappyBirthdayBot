package com.example.happybirthdaybot.service.data.impl;


import com.example.happybirthdaybot.domain.entity.ChatEntity;
import com.example.happybirthdaybot.domain.entity.UserEntity;
import com.example.happybirthdaybot.domain.entity.WishEntity;
import com.example.happybirthdaybot.dto.ChatDto;
import com.example.happybirthdaybot.dto.UserDto;
import com.example.happybirthdaybot.service.data.MapStructMapper;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.stream.Collectors;

/**
 * Реализация маппера моделей.
 *
 * @author Egor Mitrofanov.
 */
@Component
public class MapStructMapperImpl implements MapStructMapper {

    @Override
    public UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setUserId(user.getId());
        userDto.setUserTag(user.getUserName());
        userDto.setUserName(user.getFirstName());
        userDto.setUserSurname(user.getLastName());
        return userDto;
    }

    @Override
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

    @Override
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
