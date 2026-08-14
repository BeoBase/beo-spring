package com.beobase.beospring.user.internal;

import com.beobase.beospring.user.UserInfo;
import com.beobase.beospring.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserInfo findById(String id) {
        User user = userRepository.findById(id).orElseThrow();

        return new UserInfo(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    @Override
    public UserInfo createUser(String name, String email, String password) {
        // validate
        // hash password
        // generate ID
        // create User
        // save User
        // return UserInfo
        return null;
    }

}
