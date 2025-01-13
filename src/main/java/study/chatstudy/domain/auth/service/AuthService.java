package study.chatstudy.domain.auth.service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import study.chatstudy.common.exception.CustomException;
import study.chatstudy.common.exception.ErrorCode;
import study.chatstudy.domain.auth.model.request.CreateUserRequest;
import study.chatstudy.domain.auth.model.request.LoginRequest;
import study.chatstudy.domain.auth.model.response.CreateUserResponse;
import study.chatstudy.domain.auth.model.response.LoginResponse;
import study.chatstudy.domain.repository.UserRepository;
import study.chatstudy.domain.repository.entity.User;
import study.chatstudy.domain.repository.entity.UserCredentials;
import study.chatstudy.security.Hasher;
import study.chatstudy.security.JWTProvider;


import java.util.*;
import java.sql.Timestamp;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final Hasher hasher;

    @Transactional(transactionManager = "createUserTranscationManager")
    public CreateUserResponse createUser(CreateUserRequest request) {
        Optional<User> user = userRepository.findByName(request.name());

        System.out.println(request.name());
        System.out.println(request.password());

        if (user.isPresent()) {
            log.error("USER_ALREADY_EXISTS: {}", request.name());
            throw new CustomException(ErrorCode.USER_ALREADY_EXISTS);
        }

        try {

            User newUser = this.newUser(request.name());

            UserCredentials newCredentials = this.newUserCredentials(request.password(), newUser);
            newUser.setCredentials(newCredentials);

            User savedUser = userRepository.save(newUser);

            if (savedUser == null) {
                System.out.println("------------");
                throw new CustomException(ErrorCode.USER_SAVED_FAILED);
            }

        } catch (Exception e) {
            throw new CustomException(ErrorCode.USER_SAVED_FAILED, e.getMessage());
        }

        return new CreateUserResponse(request.name());
    }

    public LoginResponse login(LoginRequest request) {
        Optional<User> user = userRepository.findByName(request.name());

        if (!user.isPresent()) {
            log.error("NOT_EXIST_USER: {}", request.name());
            throw new CustomException(ErrorCode.NOT_EXIST_USER);
        }

        user.map(u -> {
            String hashedValue = hasher.getHashingValue(request.password());

            if (!u.getUserCredentials().getHashed_password().equals(hashedValue)) {
                throw new CustomException(ErrorCode.MIS_MATCH_PASSWORD);
            }

            return hashedValue;
        }).orElseThrow(() -> {
            throw new CustomException(ErrorCode.MIS_MATCH_PASSWORD);
        });

        String token = JWTProvider.createRefreshToken(request.name());
        return new LoginResponse(ErrorCode.SUCCESS, token);
    }

    public String getUserFromToken(String token) {
        return JWTProvider.getUserFromToken(token);
    }

    private User newUser(String name) {
        User newUser = User.builder()
                .name(name)
                .created_at(new Timestamp(System.currentTimeMillis()))
                .build();

        return newUser;
    }

    private UserCredentials newUserCredentials(String password, User user) {
        String hashedValue = hasher.getHashingValue(password);

        UserCredentials cre = UserCredentials.
                builder().
                user(user).
                hashed_password(hashedValue).
                build();

        return cre;
    }
}