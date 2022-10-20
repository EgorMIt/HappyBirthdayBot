package com.example.happybirthdaybot.service.impl;

import com.example.happybirthdaybot.domain.entity.ChatEntity;
import com.example.happybirthdaybot.domain.entity.FriendEntity;
import com.example.happybirthdaybot.domain.entity.NotificationLevel;
import com.example.happybirthdaybot.domain.entity.UserChatEntity;
import com.example.happybirthdaybot.domain.entity.UserEntity;
import com.example.happybirthdaybot.domain.entity.WishEntity;
import com.example.happybirthdaybot.domain.repository.ChatRepository;
import com.example.happybirthdaybot.domain.repository.FriendRepository;
import com.example.happybirthdaybot.domain.repository.UserRepository;
import com.example.happybirthdaybot.domain.repository.WishRepository;
import com.example.happybirthdaybot.dto.UserDto;
import com.example.happybirthdaybot.error.ApplicationException;
import com.example.happybirthdaybot.error.ErrorDescriptions;
import com.example.happybirthdaybot.service.UserService;
import com.example.happybirthdaybot.utils.ModelMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Реализация сервиса работы с user.
 *
 * @author Egor Mitrofanov.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    /**
     * {@link UserRepository}.
     */
    private final UserRepository userRepository;

    /**
     * {@link ChatRepository}.
     */
    private final ChatRepository chatRepository;

    /**
     * {@link WishRepository}.
     */
    private final WishRepository wishRepository;

    /**
     * {@link FriendRepository}.
     */
    private final FriendRepository friendRepository;

    /**
     * {@link ModelMapper}.
     */
    private final ModelMapper modelMapper;

    /**
     * Добавление пользователя в бд.
     *
     * @param userDto модель.
     */
    @Override
    public void createUser(UserDto userDto) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(userDto.getUserId());
        userEntity.setUserTag(userDto.getUserTag());
        userEntity.setUserName(userDto.getUserName());
        userEntity.setUserSurname(userDto.getUserSurname());
        userEntity.setIsRegistered(false);
        userEntity.setIsUpdating(false);
        userEntity.setNotificationLevel(NotificationLevel.DAY);

        userRepository.save(userEntity);
    }

    /**
     * Удалить информацию о пользователе.
     *
     * @param chatId идентификатор чата.
     */
    @Override
    public void deleteUser(Long chatId) throws ApplicationException {
        UserEntity userEntity = userRepository.findUserEntityByUserId(chatId).orElseThrow(ErrorDescriptions.APPLICATION_ERROR::exception);
        userRepository.delete(userEntity);
    }

    /**
     * Добавить информацию о пользователе.
     *
     * @param userDto модель.
     */
    @Override
    public void updateUser(UserDto userDto) throws ApplicationException {
        UserEntity userEntity = userRepository.findUserEntityByUserId(userDto.getUserId()).orElseThrow(ErrorDescriptions.APPLICATION_ERROR::exception);
        userEntity.setUserId(userDto.getUserId());
        userEntity.setUserTag(userDto.getUserTag());
        userEntity.setUserName(userDto.getUserName());
        userEntity.setUserSurname(userDto.getUserSurname());
        userEntity.setBirthdayDate(userDto.getBirthdayDate());
        userEntity.setDay(userDto.getBirthdayDate().getDayOfMonth());
        userEntity.setMonth(userDto.getBirthdayDate().getMonthValue());
        userEntity.setIsRegistered(userDto.getIsRegistered());
        userEntity.setIsUpdating(userDto.getIsUpdating());
        userEntity.setNotificationLevel(userDto.getNotificationLevel());

        userRepository.save(userEntity);
    }

    /**
     * Проверить существование пользователя в бд.
     *
     * @param userId идентификатор чата.
     */
    @Override
    public Boolean checkUser(Long userId) {
        return userRepository.findUserEntityByUserId(userId).isPresent();
    }

    /**
     * Проверить существование пользователя в бд.
     *
     * @param userTag имя пользователя.
     */
    @Override
    public Boolean checkUserByTag(String userTag) {
        return userRepository.findUserEntityByUserTag(userTag).isPresent();
    }

    /**
     * Получение пользователя по chatId.
     *
     * @param userId идентификатор чата.
     */
    public UserDto getUser(Long userId) throws ApplicationException {
        UserEntity userEntity = userRepository.findUserEntityByUserId(userId).orElseThrow(ErrorDescriptions.APPLICATION_ERROR::exception);
        return modelMapper.mapToUserDto(userEntity);
    }

    /**
     * Добавление пользователя к чату по коду.
     *
     * @param userId   идентификатор пользователя.
     * @param chatCode идентификатор чата.
     */
    @Transactional
    @Override
    public void addUserToChat(Long userId, Integer chatCode) throws ApplicationException {
        UserEntity userEntity = userRepository.findUserEntityByUserId(userId).orElseThrow(ErrorDescriptions.APPLICATION_ERROR::exception);
        ChatEntity chatEntity = chatRepository.findChatEntityByChatCode(chatCode).orElseThrow(ErrorDescriptions.APPLICATION_ERROR::exception);

        Set<UserChatEntity> users = chatEntity.getUserChat();

        if (users.stream().noneMatch(item -> item.getUser().equals(userEntity))) {
            UserChatEntity userChatEntity = new UserChatEntity();
            userChatEntity.setUser(userEntity);
            userChatEntity.setChat(chatEntity);

            users.add(userChatEntity);
            chatEntity.setUserChat(users);
            chatRepository.save(chatEntity);
        }
    }

    /**
     * Список пользователей по дню рождения.
     *
     * @param birthday день рождения.
     */
    @Override
    public List<UserDto> findUsersByBirthday(LocalDate birthday) {
        return userRepository.findUserEntitiesByDayAndMonth(birthday.getDayOfMonth(), birthday.getMonthValue())
                .stream()
                .map(modelMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    /**
     * Список пользователей у кого есть в друзьях.
     *
     * @param friendId идентификатор пользователя.
     */
    @Override
    public List<UserDto> findUsersFriendWith(Long friendId) throws ApplicationException {
        UserEntity friend = userRepository.findUserEntityByUserId(friendId).orElseThrow(ErrorDescriptions.APPLICATION_ERROR::exception);
        return friendRepository.findFriendEntitiesByFriend(friend)
                .stream()
                .map(item -> modelMapper.mapToUserDto(item.getUser()))
                .collect(Collectors.toList());
    }

    /**
     * Обновление wishlist-а пользователя.
     *
     * @param userId   идентификатор пользователя.
     * @param wishlist список пожеланий.
     */
    @Transactional
    @Override
    public void addWishlistToUser(Long userId, Set<String> wishlist) throws ApplicationException {
        UserEntity userEntity = userRepository.findUserEntityByUserId(userId).orElseThrow(ErrorDescriptions.APPLICATION_ERROR::exception);

        List<WishEntity> wishEntities = new ArrayList<>();
        for (String wish : wishlist) {
            if (!wish.isEmpty()) {
                WishEntity wishEntity = new WishEntity();
                wishEntity.setUser(userEntity);
                wishEntity.setWish(wish);

                wishEntities.add(wishEntity);
            }
        }
        if (!wishEntities.isEmpty()) {
            wishRepository.saveAll(wishEntities);
        }
    }

    /**
     * Отчистка wishlist-а пользователя.
     *
     * @param userId идентификатор пользователя.
     */
    @Transactional
    @Override
    public void clearWishlist(Long userId) throws ApplicationException {
        UserEntity userEntity = userRepository.findUserEntityByUserId(userId).orElseThrow(ErrorDescriptions.APPLICATION_ERROR::exception);

        userEntity.setWishlist(Collections.emptySet());
        List<WishEntity> wishEntities = wishRepository.findWishEntitiesByUser(userEntity);

        wishRepository.deleteAll(wishEntities);
        userRepository.save(userEntity);
    }

    /**
     * Добавление друга.
     *
     * @param userId    идентификатор пользователя.
     * @param friendTag имя пользователя друга.
     */
    @Override
    public void addFriend(Long userId, String friendTag) throws ApplicationException {
        UserEntity user = userRepository.findUserEntityByUserId(userId).orElseThrow(ErrorDescriptions.APPLICATION_ERROR::exception);
        UserEntity friend = userRepository.findUserEntityByUserTag(friendTag).orElseThrow(ErrorDescriptions.APPLICATION_ERROR::exception);

        if (user.getFriends().stream().noneMatch(item -> item.getFriend().getUserId().equals(friend.getUserId()))
                && !user.equals(friend)) {
            FriendEntity friendEntity = new FriendEntity();
            friendEntity.setUser(user);
            friendEntity.setFriend(friend);

            Set<FriendEntity> friendEntities = user.getFriends();
            friendEntities.add(friendEntity);
            user.setFriends(friendEntities);
            userRepository.save(user);
        }

    }

}
