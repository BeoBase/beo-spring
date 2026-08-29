package com.beobase.beospring.auth;

import com.beobase.beospring.auth.web.LoginRequest;
import com.beobase.beospring.auth.web.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
