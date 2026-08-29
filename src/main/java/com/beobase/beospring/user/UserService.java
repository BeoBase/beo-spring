package com.beobase.beospring.user;

import java.util.Optional;

public interface UserService {
    UserInfo findById(String id);
    UserInfo createUser(String name, String email, String password);
    Optional<UserCredentials> findCredentialsByEmail(String email);
}
