package com.userfavoritemovie.user.entity;

import lombok.*;
import org.checkerframework.common.aliasing.qual.Unique;

import javax.persistence.*;

@Entity
@ToString
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(unique = true)
    private String loginId;
    private String name;
    private String favoriteGenre;
}
