package com.userfavoritemovie.usermovieservice.service;

import com.example.userfavoritemovie.common.FavoriteGenre;
import com.example.userfavoritemovie.movie.*;
import com.example.userfavoritemovie.user.*;
import com.google.protobuf.Descriptors;
import com.userfavoritemovie.usermovieservice.dto.*;
import io.grpc.stub.StreamObserver;
import lombok.extern.log4j.Log4j2;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Log4j2
@Service
public class UserMovieService {
    @Autowired
    private UserServiceGrpc.UserServiceBlockingStub userBlockingStub;

    @Autowired
    private UserServiceGrpc.UserServiceStub userStub;

    @Autowired
    private MovieServiceGrpc.MovieServiceBlockingStub movieBlockingSub;

    @Autowired
    private MovieServiceGrpc.MovieServiceStub movieStub;

    public List<RecommendedMovie> getUserMovieSuggestion(String loginId){
        UserSearchRequest userSearchRequest = UserSearchRequest.newBuilder()
                .setLoginId(loginId)
                .build();
        for (int i = 0; i < 10; i++) {
            UserResponse userResponse0 = this.userBlockingStub.getUserFavoriteGenre(userSearchRequest);
            System.out.println(userResponse0.getName() + i);
        }
        UserResponse userResponse = this.userBlockingStub.getUserFavoriteGenre(userSearchRequest);
        MovieSearchRequest movieSearchRequest = MovieSearchRequest.newBuilder().setGenre(userResponse.getFavoriteGenre()).build();

        for (int i = 0; i < 10; i++) {
            MovieSearchResponse movieSearchResponse0 = this.movieBlockingSub.getMovies(movieSearchRequest);
            System.out.println("request for movie" + i);
        }

        MovieSearchResponse movieSearchResponse = this.movieBlockingSub.getMovies(movieSearchRequest);
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
        UserResponse userResponse = this.userBlockingStub.updateUserFavoriteGenre(userFavoriteGenreUpdateRequest);
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
        UserResponse userResponse = this.userBlockingStub.createUser(createUserRequest);
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
        MovieDto movieDto = this.movieBlockingSub.addMovie(addMovieRequest);
        RecommendedMovie recommendedMovie = new RecommendedMovie(movieDto.getTitle(), movieDto.getYear(), movieDto.getRating());
        return recommendedMovie;
    }

    public List<Map<Descriptors.FieldDescriptor, Object>> getMovieStream(String genre) throws InterruptedException {
        MovieSearchRequest movieSearchRequest = MovieSearchRequest.newBuilder()
                .setGenre(FavoriteGenre.valueOf(genre.toUpperCase()))
                .build();
        List<Map<Descriptors.FieldDescriptor, Object>> response = new ArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        this.movieStub.getMovieStream(movieSearchRequest, new StreamObserver<MovieDto>() {
            @Override
            public void onNext(MovieDto movieDto) {
                response.add(movieDto.getAllFields());
                log.info("Get movie, {}", movieDto);
            }

            @Override
            public void onError(Throwable t) {
                countDownLatch.countDown();
            }

            @Override
            public void onCompleted() {
                countDownLatch.countDown();
            }
        });
        boolean await = countDownLatch.await(1, TimeUnit.MINUTES);
//        return await ? response : Collections.emptyList();
        return response;
//        this.movieBlockingSub.getMovieStream(movieSearchRequest)
//                .forEachRemaining(movieDto -> response.add(movieDto.getAllFields()));
//        return response;
    }
    public RecommendedMovie decreaseRating(DecreaseRating decreaseRating) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        DecreaseRatingRequest decreaseRatingRequest = DecreaseRatingRequest.newBuilder()
                .setTitle(decreaseRating.getTitle())
                .setDecreaseRating(decreaseRating.getDecreaseRating())
                .build();
        RecommendedMovie recommendedMovie = new RecommendedMovie();
        StreamObserver<DecreaseRatingRequest> requestStreamObserver = this.movieStub.decreaseRating(new StreamObserver<MovieDto>() {
            @Override
            public void onNext(MovieDto movieDto) {
                recommendedMovie.setTitle(movieDto.getTitle());
                recommendedMovie.setRating(movieDto.getRating());
                recommendedMovie.setYear(movieDto.getYear());
                log.info("movieDto: {}", recommendedMovie);
            }

            @Override
            public void onError(Throwable t) {
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });
        for (int i = 0; i<5; i++){
            requestStreamObserver.onNext(decreaseRatingRequest);
        }
        requestStreamObserver.onCompleted();
        latch.await();
        return recommendedMovie;
    }
}
