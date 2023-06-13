package com.userfavoritemovie.usermovieservice.service;

import com.example.userfavoritemovie.common.FavoriteGenre;
import com.example.userfavoritemovie.movie.AddMovieRequest;
import com.example.userfavoritemovie.movie.MovieSearchRequest;
import com.example.userfavoritemovie.movie.MovieSearchResponse;
import com.example.userfavoritemovie.movie.MovieServiceGrpc;
import com.example.userfavoritemovie.user.*;
import com.userfavoritemovie.usermovieservice.dto.*;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserMovieService {
    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub userStub;

    @GrpcClient("movie-service")
    private MovieServiceGrpc.MovieServiceBlockingStub movieStub;

    public List<RecommendedMovie> getUserMovieSuggestion(String loginId){
        UserSearchRequest userSearchRequest = UserSearchRequest.newBuilder()
                .setLoginId(loginId)
                .build();
        UserResponse userResponse = this.userStub.getUserFavoriteGenre(userSearchRequest);
        MovieSearchRequest movieSearchRequest = MovieSearchRequest.newBuilder().setGenre(userResponse.getFavoriteGenre()).build();
        MovieSearchResponse movieSearchResponse = this.movieStub.getMovies(movieSearchRequest);
        return movieSearchResponse.getMovieList()
                .stream()
                .map(movieDto -> new RecommendedMovie(movieDto.getTitle(), movieDto.getYear(), movieDto.getRating()))
                .collect(Collectors.toList());

    }

    public UserDTO setUserGenre(UserGenre userGenre){
        UserFavoriteGenreUpdateRequest userFavoriteGenreUpdateRequest = UserFavoriteGenreUpdateRequest.newBuilder()
                .setLoginId(userGenre.getLoginId())
                .setFavoriteGenre(FavoriteGenre.valueOf(userGenre.getGenre().toUpperCase()))
                .build();
        UserResponse userResponse = this.userStub.updateUserFavoriteGenre(userFavoriteGenreUpdateRequest);
        return UserDTO.builder()
                .loginId(userResponse.getLoginId())
                .name(userResponse.getName())
                .favoriteGenre(userResponse.getFavoriteGenre().toString())
                .build();
    }

    public UserDTO createUser(NewUser newUser){
        CreateUserRequest createUserRequest = CreateUserRequest.newBuilder()
                .setLoginId(newUser.getLoginId())
                .setName(newUser.getName())
                .build();
        UserResponse userResponse = this.userStub.createUser(createUserRequest);
        return UserDTO.builder()
                .loginId(userResponse.getLoginId())
                .name(userResponse.getName())
                .favoriteGenre(userResponse.getFavoriteGenre().toString())
                .build();
    }

    public RecommendedMovie addMovie(NewMovie newMovie){
        AddMovieRequest addMovieRequest = AddMovieRequest.newBuilder()
                .setTitle(newMovie.getTitle())
                .setReleaseYear(newMovie.getYear())
                .setRating(newMovie.getRating())
                .setGenre(FavoriteGenre.valueOf(newMovie.getGenre().toUpperCase()))
                .build();
        this.movieStub.addMovie(addMovieRequest);
        RecommendedMovie recommendedMovie = new RecommendedMovie(newMovie.getTitle(), newMovie.getYear(), newMovie.getRating());
        return recommendedMovie;
    }
}
