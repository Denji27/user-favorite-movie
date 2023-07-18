package com.example.entity;

import lombok.*;

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
    private String username;
    private String password;
    private String role;
    @Column(unique = true)
    private String loginId;
    private String name;
    private String favoriteGenre;

}
