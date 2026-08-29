package com.beobase.beospring.user;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserEventHandlerTest {

    @Mock
    private UserService userService;

    private UserEventHandler userEventHandler;

    @BeforeEach
    void setUp() {
        userEventHandler = new UserEventHandler(userService);
    }

    @Test
    void onShouldCreateUserFromEvent() {
        UserRegistrationRequested event = new UserRegistrationRequested(
                "John Doe",
                "john@example.com",
                "password123"
        );

        userEventHandler.on(event);

        verify(userService).createUser(
                "John Doe",
                "john@example.com",
                "password123"
        );
    }
}