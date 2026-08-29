package com.beobase.beospring.user;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class UserEventHandler {

    private final UserService userService;

    @EventListener
    void on(UserRegistrationRequested event) {
        userService.createUser(
                event.name(),
                event.email(),
                event.password()
        );
    }
}