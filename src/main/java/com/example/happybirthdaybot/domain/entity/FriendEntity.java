package com.example.happybirthdaybot.domain.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Зависимость пользователя и чата.
 *
 * @author Egor Mitrofanov.
 */
@Getter
@Setter
@Entity
@Table(name = "friend")
public class FriendEntity {

    /**
     * Идентификатор.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence")
    @SequenceGenerator(name = "sequence", allocationSize = 1, sequenceName = "friend_seq")
    private Long id;

    /**
     * Пользователь.
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    /**
     * Чат.
     */
    @ManyToOne
    @JoinColumn(name = "friend_id")
    private UserEntity friend;

}
