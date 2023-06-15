package com.userfavoritemovie.movie.service;

import com.example.userfavoritemovie.movie.*;
import com.userfavoritemovie.movie.entity.Movie;
import com.userfavoritemovie.movie.repository.MovieRepository;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@GrpcService
public class MovieService extends MovieServiceGrpc.MovieServiceImplBase {
    @Autowired
    private MovieRepository movieRepo;

    @Override
    public void getMovies(MovieSearchRequest request, StreamObserver<MovieSearchResponse> responseObserver) {
        List<MovieDto> movieDtoList = this.movieRepo.getMovieByGenreOrderByYearDesc(request.getGenre().toString())
                .stream()
                .map(movie -> MovieDto.newBuilder()
                        .setTitle(movie.getTitle())
                        .setYear(movie.getYear())
                        .setRating(movie.getRating())
                        .build())
                .collect(Collectors.toList());
        responseObserver.onNext(MovieSearchResponse.newBuilder().addAllMovie(movieDtoList).build());
        responseObserver.onCompleted();
    }

    @Override
    public void addMovie(AddMovieRequest request, StreamObserver<MovieDto> responseObserver) {
        Movie movie = Movie.builder()
                .title(request.getTitle())
                .year(request.getReleaseYear())
                .rating(request.getRating())
                .genre(request.getGenre().toString())
                .build();
        MovieDto movieDto = MovieDto.newBuilder()
                .setTitle(request.getTitle())
                .setYear(request.getReleaseYear())
                .setRating(request.getRating())
                .build();
        this.movieRepo.save(movie);
        responseObserver.onNext(movieDto);
        responseObserver.onCompleted();
    }

    @Override
    public void getMovieStream(MovieSearchRequest request, StreamObserver<MovieDto> responseObserver) {
        List<Movie> movieList = this.movieRepo.getMovieByGenreOrderByYearDesc(request.getGenre().toString());
        for (Movie movie : movieList){
            MovieDto movieDto = MovieDto.newBuilder()
                    .setTitle(movie.getTitle())
                    .setYear(movie.getYear())
                    .setRating(movie.getRating())
                    .build();
            responseObserver.onNext(movieDto);
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<DecreaseRatingRequest> decreaseRating(StreamObserver<MovieDto> movieDtoStreamObserver) {
        return new StreamObserver<DecreaseRatingRequest>() {
            private String title;
            private int year;
            private double rating;
            @Override
            public void onNext(DecreaseRatingRequest decreaseRatingRequest) {
                String title = decreaseRatingRequest.getTitle();
                double decreaseRating = decreaseRatingRequest.getDecreaseRating();
                Movie movie = movieRepo.findMovieByTitle(title);
                this.rating = movie.getRating() - decreaseRating;
                movie.setRating(rating);
                movieRepo.save(movie);
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
                movieDtoStreamObserver.onNext(movieDto);
                movieDtoStreamObserver.onCompleted();
            }
        };
    }
}
