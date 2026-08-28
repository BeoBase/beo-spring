package com.beobase.beospring.user.web;

import com.beobase.beospring.user.UserInfo;
import com.beobase.beospring.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
class UserController {

    private final UserService userService;

    @PostMapping
    ResponseEntity<UserInfo> createUser(@RequestBody UserRegistrationRequest request) {
        UserInfo userInfo = userService.createUser(
                request.name(),
                request.email(),
                request.password()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(userInfo);
    }
}
