package com.userfavoritemovie.usermovieservice.controller;

import com.google.protobuf.Descriptors;
import com.userfavoritemovie.usermovieservice.dto.*;
import com.userfavoritemovie.usermovieservice.service.UserMovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @GetMapping("/movie/{genre}")
    public List<Map<Descriptors.FieldDescriptor, Object>> getMovieStream(@PathVariable String genre) throws InterruptedException {
        return this.userMovieService.getMovieStream(genre);
    }

    @PutMapping("/movie/decrease-rating")
    public ResponseEntity<RecommendedMovie> decreaseRating(@RequestBody DecreaseRating decreaseRating) throws InterruptedException {
        return ResponseEntity.ok(this.userMovieService.decreaseRating(decreaseRating));
    }

}
