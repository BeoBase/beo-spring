package com.beobase.beospring.auth.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.beobase.beospring.auth.web.LoginRequest;
import com.beobase.beospring.auth.web.LoginResponse;
import com.beobase.beospring.shared.PasswordService;
import com.beobase.beospring.shared.TokenService;
import com.beobase.beospring.user.UserCredentials;
import com.beobase.beospring.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordService passwordService;

    @Mock
    private TokenService tokenService;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(
                userService,
                passwordService,
                tokenService
        );
    }

    @Test
    void loginShouldReturnTokenWhenCredentialsAreValid() {
        UserCredentials credentials = new UserCredentials(
                "abc123",
                "john@example.com",
                "ROLE_USER",
                "hashed-password"
        );

        when(userService.findCredentialsByEmail("john@example.com"))
                .thenReturn(Optional.of(credentials));
        when(passwordService.matches("password123", "hashed-password"))
                .thenReturn(true);
        when(tokenService.generate("abc123", "john@example.com", "ROLE_USER"))
                .thenReturn("jwt-token");

        LoginResponse response = authService.login(
                new LoginRequest("john@example.com", "password123")
        );

        assertTrue(response.success());
        assertEquals("jwt-token", response.token());
        assertEquals("abc123", response.userId());
        assertEquals("ROLE_USER", response.role());

        verify(tokenService).generate("abc123", "john@example.com", "ROLE_USER");
    }

    @Test
    void loginShouldThrowWhenUserDoesNotExist() {
        when(userService.findCredentialsByEmail("john@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> authService.login(
                        new LoginRequest("john@example.com", "password123")
                )
        );
    }

    @Test
    void loginShouldThrowWhenPasswordDoesNotMatch() {
        UserCredentials credentials = new UserCredentials(
                "abc123",
                "john@example.com",
                "ROLE_USER",
                "hashed-password"
        );

        when(userService.findCredentialsByEmail("john@example.com"))
                .thenReturn(Optional.of(credentials));
        when(passwordService.matches("wrong-password", "hashed-password"))
                .thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.login(
                        new LoginRequest("john@example.com", "wrong-password")
                )
        );

        assertEquals("Invalid email or password", exception.getMessage());
    }
}
