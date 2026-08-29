package com.beobase.beospring.auth.internal;

import com.beobase.beospring.auth.AuthService;
import com.beobase.beospring.auth.web.LoginRequest;
import com.beobase.beospring.auth.web.LoginResponse;
import com.beobase.beospring.auth.web.RegisterRequest;
import com.beobase.beospring.shared.PasswordService;
import com.beobase.beospring.shared.TokenService;
import com.beobase.beospring.user.UserCredentials;
import com.beobase.beospring.user.UserRegistrationRequested;
import com.beobase.beospring.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordService passwordService;
    private final TokenService tokenService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void register(RegisterRequest request) {
        eventPublisher.publishEvent(new UserRegistrationRequested(
                request.name(),
                request.email(),
                request.password()
        ));
    }

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
