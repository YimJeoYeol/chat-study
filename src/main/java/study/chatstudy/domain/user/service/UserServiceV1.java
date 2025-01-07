package study.chatstudy.domain.user.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import study.chatstudy.common.exception.ErrorCode;
import study.chatstudy.domain.repository.UserRepository;
import study.chatstudy.domain.user.model.response.UserSearchResponse;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceV1 {

    private final UserRepository userRepository;

    public final UserSearchResponse searchUser(String name, String user) {
        List<String> names = userRepository.findNameByNameMatch(name,user);
        return new UserSearchResponse(ErrorCode.SUCCESS, names);
    }
}
