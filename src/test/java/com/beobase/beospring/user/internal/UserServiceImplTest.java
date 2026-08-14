package com.beobase.beospring.user.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.beobase.beospring.user.UserInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void findByIdShouldReturnUserInfoWhenUserExists() {
        User user = new User();
        user.setId("abc123");
        user.setName("John Doe");
        user.setEmail("john@example.com");

        when(userRepository.findById("abc123"))
                .thenReturn(Optional.of(user));

        UserInfo result = userService.findById("abc123");

        assertEquals("abc123", result.id());
        assertEquals("John Doe", result.name());
        assertEquals("john@example.com", result.email());

        verify(userRepository).findById("abc123");
    }

    @Test
    void findByIdShouldThrowExceptionWhenUserDoesNotExist() {
        when(userRepository.findById("abc123"))
                .thenReturn(Optional.empty());

        assertThrows(
                java.util.NoSuchElementException.class,
                () -> userService.findById("abc123")
        );

        verify(userRepository).findById("abc123");
    }
}
