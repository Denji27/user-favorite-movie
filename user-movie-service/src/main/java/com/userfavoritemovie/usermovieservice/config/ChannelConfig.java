package com.userfavoritemovie.usermovieservice.config;

import com.example.userfavoritemovie.movie.MovieServiceGrpc;
import com.example.userfavoritemovie.user.UserServiceGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.NameResolverRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ChannelConfig {
    public Channel channelMovie(){
        List<String> instances = new ArrayList<>();
        instances.add("localhost:7575");
        instances.add("localhost:8585");
        ServiceRegistry.register("movie-service", instances);
        NameResolverRegistry.getDefaultRegistry().register(new TempNameResolverProvider());

        ManagedChannel managedChannel = ManagedChannelBuilder
                .forTarget("http://movie-service")
                .defaultLoadBalancingPolicy("round_robin")
                .usePlaintext()
                .build();
        return managedChannel;
    }

    public Channel channelUser(){
        List<String> instances = new ArrayList<>();
        instances.add("localhost:6565");
        instances.add("localhost:9595");
        ServiceRegistry.register("user-service", instances);
        NameResolverRegistry.getDefaultRegistry().register(new TempNameResolverProvider());

        ManagedChannel managedChannel = ManagedChannelBuilder
                .forTarget("http://user-service")
                .defaultLoadBalancingPolicy("round_robin")
                .usePlaintext()
                .build();
        return managedChannel;
    }

    @Bean
    public MovieServiceGrpc.MovieServiceBlockingStub movieBlockingStub(){
        return MovieServiceGrpc.newBlockingStub(channelMovie());
    }

    @Bean
    public MovieServiceGrpc.MovieServiceStub movieStub(){
        return MovieServiceGrpc.newStub(channelMovie());
    }

    @Bean
    public UserServiceGrpc.UserServiceBlockingStub userBlockingStub(){
        return UserServiceGrpc.newBlockingStub(channelUser());
    }

    @Bean
    public UserServiceGrpc.UserServiceStub userStub(){
        return UserServiceGrpc.newStub(channelUser());
    }
}
