package com.beobase.beospring.user;

public interface UserService {
    UserInfo findById(String id);
    UserInfo createUser(String name, String email, String password);
}
