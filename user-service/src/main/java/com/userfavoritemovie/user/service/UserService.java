package com.userfavoritemovie.user.service;

import com.example.userfavoritemovie.common.FavoriteGenre;
import com.example.userfavoritemovie.user.*;
import com.userfavoritemovie.user.entity.User;
import com.userfavoritemovie.user.repository.UserRepository;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class UserService extends UserServiceGrpc.UserServiceImplBase {
    @Autowired
    private UserRepository userRepo;

    @Override
    public void getUserFavoriteGenre(UserSearchRequest request, StreamObserver<UserResponse> responseObserver) {
        User user = this.userRepo.findByLoginId(request.getLoginId());
        UserResponse userResponse = UserResponse.newBuilder()
                .setName(user.getName())
                .setLoginId(user.getLoginId())
                .setFavoriteGenre(FavoriteGenre.valueOf(user.getFavoriteGenre().toUpperCase()))
                .build();
        responseObserver.onNext(userResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void updateUserFavoriteGenre(UserFavoriteGenreUpdateRequest request, StreamObserver<UserResponse> responseObserver) {
        User user = this.userRepo.findByLoginId(request.getLoginId());
        user.setFavoriteGenre(request.getFavoriteGenre().toString());
        userRepo.save(user);
        UserResponse userResponse = UserResponse.newBuilder()
                .setName(user.getName())
                .setLoginId(user.getLoginId())
                .setFavoriteGenre(FavoriteGenre.valueOf(user.getFavoriteGenre().toUpperCase()))
                .build();
        responseObserver.onNext(userResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void createUser(CreateUserRequest request, StreamObserver<UserResponse> responseObserver) {
        UserResponse userResponse = UserResponse.newBuilder()
                .setLoginId(request.getLoginId())
                .setName(request.getName())
                .build();
        User user = User.builder()
                .loginId(userResponse.getLoginId())
                .name(userResponse.getName())
                .favoriteGenre(userResponse.getFavoriteGenre().toString())
                .build();
        this.userRepo.save(user);
        responseObserver.onNext(userResponse);
        responseObserver.onCompleted();
    }
}
