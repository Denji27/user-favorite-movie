package com.userfavoritemovie.user.repository;

import com.userfavoritemovie.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByLoginId(String loginId);
}
