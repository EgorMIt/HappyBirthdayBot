package com.example.happybirthdaybot.domain.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.Set;

/**
 * Сущность пользователя.
 *
 * @author Egor Mitrofanov.
 */
@Getter
@Setter
@Entity
@Table(name = "user_table")
public class UserEntity {

    /**
     * Идентификатор.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence")
    @SequenceGenerator(name = "sequence", allocationSize = 1, sequenceName = "user_table_seq")
    private Long id;

    /**
     * Идентификатор пользователя.
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * Тег пользователя.
     */
    @Column(name = "user_tag")
    private String userTag;

    /**
     * Имя пользователя.
     */
    @Column(name = "user_name")
    private String userName;

    /**
     * Фамилия пользователя.
     */
    @Column(name = "user_surname")
    private String userSurname;

    /**
     * День рождения.
     */
    @Column(name = "birthday")
    private LocalDate birthdayDate;

    /**
     * День.
     */
    @Column(name = "birthday_day")
    private Integer day;

    /**
     * Месяц.
     */
    @Column(name = "birthday_month")
    private Integer month;

    /**
     * Флаг заполненности даты.
     */
    @Column(name = "is_registered")
    private Boolean isRegistered;

    /**
     * Флаг изменения wishlist-а.
     */
    @Column(name = "is_updating")
    private Boolean isUpdating;

    /**
     * Настройка уведомлений.
     */
    @Column(name = "notification_level")
    @Enumerated(EnumType.ORDINAL)
    private NotificationLevel notificationLevel;

    /**
     * Чаты юзера.
     */
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    Set<UserChatEntity> userChat;

    /**
     * Чаты юзера.
     */
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    Set<FriendEntity> friends;

    /**
     * wishlist юзера.
     */
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    Set<WishEntity> wishlist;
}
