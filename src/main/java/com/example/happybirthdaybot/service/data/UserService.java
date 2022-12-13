package com.example.happybirthdaybot.service.data;

import com.example.happybirthdaybot.dto.UserDto;
import com.example.happybirthdaybot.error.ApplicationException;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * Интерфейс сервиса работы с user.
 *
 * @author Egor Mitrofanov.
 */
public interface UserService {

    /**
     * Добавление пользователя в бд.
     *
     * @param userDto модель.
     */
    void createUser(UserDto userDto);

    /**
     * Удалить информацию о пользователе.
     *
     * @param chatId идентификатор пользователя.
     */
    void deleteUser(Long chatId) throws ApplicationException;

    /**
     * Добавить информацию о пользователе.
     *
     * @param userDto модель.
     */
    void updateUser(UserDto userDto) throws ApplicationException;

    /**
     * Проверить существование пользователя в бд.
     *
     * @param userId идентификатор пользователя.
     */
    Boolean checkUser(Long userId);

    /**
     * Проверить существование пользователя в бд.
     *
     * @param userTag имя пользователя.
     */
    Boolean checkUserByTag(String userTag);

    /**
     * Получение пользователя по userId.
     *
     * @param userId идентификатор пользователя.
     */
    UserDto getUser(Long userId) throws ApplicationException;

    /**
     * Добавление пользователя к чату по коду.
     *
     * @param userId   идентификатор пользователя.
     * @param chatCode идентификатор чата.
     */
    void addUserToChat(Long userId, Integer chatCode) throws ApplicationException;

    /**
     * Список пользователей по дню рождения.
     *
     * @param birthday день рождения.
     */
    List<UserDto> findUsersByBirthday(LocalDate birthday);

    /**
     * Список пользователей у кого есть в друзьях.
     *
     * @param friendId идентификатор пользователя.
     */
    List<UserDto> findUsersFriendWith(Long friendId) throws ApplicationException;

    /**
     * Обновление wishlist-а пользователя.
     *
     * @param userId   идентификатор пользователя.
     * @param wishlist список пожеланий.
     */
    void addWishlistToUser(Long userId, Set<String> wishlist) throws ApplicationException;

    /**
     * Отчистка wishlist-а пользователя.
     *
     * @param userId идентификатор пользователя.
     */
    void clearWishlist(Long userId) throws ApplicationException;

    /**
     * Добавление друга.
     *
     * @param userId    идентификатор пользователя.
     * @param friendTag имя пользователя друга.
     */
    void addFriend(Long userId, String friendTag) throws ApplicationException;

}
