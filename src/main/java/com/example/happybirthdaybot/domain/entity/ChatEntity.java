package com.example.happybirthdaybot.domain.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Set;

/**
 * Зависимость пользователя и чата.
 *
 * @author Egor Mitrofanov.
 */
@Getter
@Setter
@Entity
@Table(name = "chat_table")
public class ChatEntity {

    /**
     * Идентификатор.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence")
    @SequenceGenerator(name = "sequence", allocationSize = 1, sequenceName = "chat_table_seq")
    private Long id;

    /**
     * Идентификатор чата.
     */
    @Column(name = "chat_id")
    private Long chatId;

    /**
     * Код инвайта.
     */
    @Column(name = "chat_code")
    private Integer chatCode;

    /**
     * Пользователи в чате.
     */
    @OneToMany(mappedBy = "chat", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    Set<UserChatEntity> userChat;

}
