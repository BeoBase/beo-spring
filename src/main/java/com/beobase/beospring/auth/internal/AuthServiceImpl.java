package com.beobase.beospring.auth.internal;

import com.beobase.beospring.auth.AuthService;
import com.beobase.beospring.auth.web.LoginRequest;
import com.beobase.beospring.auth.web.LoginResponse;
import com.beobase.beospring.shared.PasswordService;
import com.beobase.beospring.shared.TokenService;
import com.beobase.beospring.user.UserCredentials;
import com.beobase.beospring.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordService passwordService;
    private final TokenService tokenService;

    @Override
    public LoginResponse login(LoginRequest request) {

        UserCredentials credentials = userService.findCredentialsByEmail(request.email())
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid email or password"));

        if (!passwordService.matches(
                request.password(),
                credentials.passwordHashed()
        )) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = tokenService.generate(
                credentials.id(),
                credentials.email(),
                credentials.role()
        );

        return new LoginResponse(
                true,
                token,
                credentials.id(),
                credentials.role()
        );
    }
}
