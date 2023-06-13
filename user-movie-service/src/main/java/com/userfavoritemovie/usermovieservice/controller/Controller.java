package com.userfavoritemovie.usermovieservice.controller;

import com.userfavoritemovie.usermovieservice.dto.*;
import com.userfavoritemovie.usermovieservice.service.UserMovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class Controller {
    @Autowired
    private UserMovieService userMovieService;

    // lấy ra những bộ phim theo thể loại yêu thích của user
    @GetMapping("user/{loginId}")
    public List<RecommendedMovie> getMovies(@PathVariable String loginId){
        return this.userMovieService.getUserMovieSuggestion(loginId);
    }

    // update lại thể loại yêu thích của user
    @PutMapping("/user")
    public ResponseEntity<UserDTO> setUserGenre(@RequestBody UserGenre userGenre){
        return ResponseEntity.ok(this.userMovieService.setUserGenre(userGenre));
    }

    // tạo một user mới với thể loại yêu thích mặc định là drama
    @PutMapping("/new-user")
    public ResponseEntity<UserDTO> createUser(@RequestBody NewUser newUser){
        return ResponseEntity.ok(this.userMovieService.createUser(newUser));
    }

    // thêm một bộ phim mới (add a new film)
    @PutMapping("/movie")
    public ResponseEntity<RecommendedMovie> addMovie(@RequestBody NewMovie newMovie){
        return ResponseEntity.ok(this.userMovieService.addMovie(newMovie));
    }

}
