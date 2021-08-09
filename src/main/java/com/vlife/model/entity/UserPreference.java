package com.vlife.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@Table(name = "user_preference", schema = "vlife")
public class UserPreference {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long telegramUserId;

    @Column
    private String breed;

    @Column
    private Boolean isLiked;
}
