package com.userfavoritemovie.usermovieservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewMovie {
    private String title;
    private int year;
    private double rating;
    private String genre;
}
