package com.beobase.beospring.auth;

import com.beobase.beospring.auth.web.LoginRequest;
import com.beobase.beospring.auth.web.LoginResponse;
import com.beobase.beospring.auth.web.RegisterRequest;

public interface AuthService {
    LoginResponse login(LoginRequest request);

    void register(RegisterRequest request);
}