package com.userfavoritemovie.movie.service;

import com.example.userfavoritemovie.movie.DecreaseRatingRequest;
import com.example.userfavoritemovie.movie.MovieDto;
import com.userfavoritemovie.movie.entity.Movie;
import com.userfavoritemovie.movie.repository.MovieRepository;
import io.grpc.stub.StreamObserver;

public class RatingStreamRequest implements StreamObserver<DecreaseRatingRequest> {
    private StreamObserver<MovieDto> movieDtoStreamObserver;
    private String title;
    private int year;
    private double rating;
    private MovieRepository movieRepository;
    public RatingStreamRequest(StreamObserver<MovieDto> movieDtoStreamObserver) {
        this.movieDtoStreamObserver = movieDtoStreamObserver;
    }

    @Override
    public void onNext(DecreaseRatingRequest decreaseRatingRequest) {
        String title = decreaseRatingRequest.getTitle();
        double decreaseRating = decreaseRatingRequest.getDecreaseRating();
        Movie movie = movieRepository.findMovieByTitle(title);
        this.rating = movie.getRating() - decreaseRating;
        movie.setRating(rating);
        movieRepository.save(movie);
        this.title = movie.getTitle();
        this.year = movie.getYear();
    }

    @Override
    public void onError(Throwable t) {

    }

    @Override
    public void onCompleted() {
        MovieDto movieDto = MovieDto.newBuilder()
                .setTitle(this.title).setRating(this.rating).setYear(this.year)
                .build();
        this.movieDtoStreamObserver.onNext(movieDto);
        this.movieDtoStreamObserver.onCompleted();
    }
}
