package com.example.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@ToString
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String title;
    @Column(name = "release_year")
    private int year;
    private double rating;
    private String genre;
}
