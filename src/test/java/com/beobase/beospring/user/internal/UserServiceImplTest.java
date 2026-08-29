package com.beobase.beospring.user.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.beobase.beospring.shared.IdGenerator;
import com.beobase.beospring.shared.PasswordService;
import com.beobase.beospring.shared.StringService;
import com.beobase.beospring.user.UserCredentials;
import com.beobase.beospring.user.UserInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private StringService stringService;

    @Mock
    private PasswordService passwordService;

    @Mock
    private IdGenerator idGenerator;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(
                userRepository,
                stringService,
                passwordService,
                idGenerator
        );
    }

    @Test
    void findByIdShouldReturnUserInfoWhenUserExists() {
        User user = new User();
        user.setId("abc123");
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setRole(Role.ROLE_USER);

        when(userRepository.findById("abc123"))
                .thenReturn(Optional.of(user));

        UserInfo result = userService.findById("abc123");

        assertEquals("abc123", result.id());
        assertEquals("John Doe", result.name());
        assertEquals("john@example.com", result.email());
        assertEquals(Role.ROLE_USER.name(), result.role());

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

    @Test
    void createUserShouldThrowExceptionWhenEmailIsNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(
                        "John Doe",
                        null,
                        "password123"
                )
        );

        verifyNoInteractions(
                stringService,
                userRepository,
                passwordService,
                idGenerator
        );
    }

    @Test
    void createUserShouldThrowExceptionWhenEmailIsBlank() {
        assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(
                        "John Doe",
                        "   ",
                        "password123"
                )
        );

        verifyNoInteractions(
                stringService,
                userRepository,
                passwordService,
                idGenerator
        );
    }

    @Test
    void createUserShouldThrowExceptionWhenEmailAlreadyExists() {
        when(stringService.normalizeString(" JOHN@EXAMPLE.COM "))
                .thenReturn("john@example.com");

        when(userRepository.existsByEmailIgnoreCase("john@example.com"))
                .thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(
                        "John Doe",
                        " JOHN@EXAMPLE.COM ",
                        "password123"
                )
        );

        assertEquals(
                "Email already exists: john@example.com",
                exception.getMessage()
        );

        verify(stringService).normalizeString(" JOHN@EXAMPLE.COM ");
        verify(userRepository).existsByEmailIgnoreCase("john@example.com");

        verifyNoInteractions(passwordService, idGenerator);
    }

    @Test
    void createUserShouldThrowExceptionWhenPasswordIsNull() {
        when(stringService.normalizeString("john@example.com"))
                .thenReturn("john@example.com");

        when(userRepository.existsByEmailIgnoreCase("john@example.com"))
                .thenReturn(false);

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(
                        "John Doe",
                        "john@example.com",
                        null
                )
        );

        verify(stringService).normalizeString("john@example.com");
        verify(userRepository).existsByEmailIgnoreCase("john@example.com");

        verifyNoInteractions(
                passwordService,
                idGenerator
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUserShouldThrowExceptionWhenPasswordIsBlank() {
        when(stringService.normalizeString("john@example.com"))
                .thenReturn("john@example.com");

        when(userRepository.existsByEmailIgnoreCase("john@example.com"))
                .thenReturn(false);

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(
                        "John Doe",
                        "john@example.com",
                        "   "
                )
        );

        verify(stringService).normalizeString("john@example.com");
        verify(userRepository).existsByEmailIgnoreCase("john@example.com");

        verifyNoInteractions(
                passwordService,
                idGenerator
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUserShouldCreateAndReturnUserInfo() {
        when(stringService.normalizeString(" JOHN@EXAMPLE.COM "))
                .thenReturn("john@example.com");

        when(userRepository.existsByEmailIgnoreCase("john@example.com"))
                .thenReturn(false);

        when(passwordService.hash("password123"))
                .thenReturn("hashed-password");

        when(idGenerator.generate())
                .thenReturn("abc123");

        User savedUser = new User();
        savedUser.setId("abc123");
        savedUser.setName("John Doe");
        savedUser.setEmail("john@example.com");
        savedUser.setPasswordHashed("hashed-password");
        savedUser.setRole(Role.ROLE_USER);

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        UserInfo result = userService.createUser(
                "John Doe",
                " JOHN@EXAMPLE.COM ",
                "password123"
        );

        assertEquals("abc123", result.id());
        assertEquals("John Doe", result.name());
        assertEquals("john@example.com", result.email());

        verify(stringService).normalizeString(" JOHN@EXAMPLE.COM ");
        verify(userRepository).existsByEmailIgnoreCase("john@example.com");
        verify(passwordService).hash("password123");
        verify(idGenerator).generate();
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUserShouldNormalizeEmailBeforeSaving() {
        when(stringService.normalizeString(" JOHN@EXAMPLE.COM "))
                .thenReturn("john@example.com");

        when(userRepository.existsByEmailIgnoreCase("john@example.com"))
                .thenReturn(false);

        when(passwordService.hash("password123"))
                .thenReturn("hashed-password");

        when(idGenerator.generate())
                .thenReturn("abc123");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserInfo result = userService.createUser(
                "John Doe",
                " JOHN@EXAMPLE.COM ",
                "password123"
        );

        assertEquals("john@example.com", result.email());
    }


    @Test
    void createUserShouldHashPasswordBeforeSaving() {
        when(stringService.normalizeString("john@example.com"))
                .thenReturn("john@example.com");

        when(userRepository.existsByEmailIgnoreCase("john@example.com"))
                .thenReturn(false);

        when(passwordService.hash("password123"))
                .thenReturn("hashed-password");

        when(idGenerator.generate())
                .thenReturn("abc123");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        userService.createUser(
                "John Doe",
                "john@example.com",
                "password123"
        );

        verify(passwordService).hash("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUserShouldRetryWhenIdCollides() {
        when(stringService.normalizeString("john@example.com"))
                .thenReturn("john@example.com");

        when(userRepository.existsByEmailIgnoreCase("john@example.com"))
                .thenReturn(false);

        when(passwordService.hash("password123"))
                .thenReturn("hashed-password");

        when(idGenerator.generate())
                .thenReturn("collision-id")
                .thenReturn("unique-id");

        User savedUser = new User();
        savedUser.setId("unique-id");
        savedUser.setName("John Doe");
        savedUser.setEmail("john@example.com");
        savedUser.setRole(Role.ROLE_USER);

        when(userRepository.save(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate key"))
                .thenReturn(savedUser);

        UserInfo result = userService.createUser(
                "John Doe",
                "john@example.com",
                "password123"
        );

        assertEquals("unique-id", result.id());

        verify(idGenerator, times(2)).generate();
        verify(userRepository, times(2)).save(any(User.class));
    }

    @Test
    void createUserShouldThrowAfterFiveIdCollisions() {
        when(stringService.normalizeString("john@example.com"))
                .thenReturn("john@example.com");

        when(userRepository.existsByEmailIgnoreCase("john@example.com"))
                .thenReturn(false);

        when(passwordService.hash("password123"))
                .thenReturn("hashed-password");

        when(idGenerator.generate())
                .thenReturn("id-1")
                .thenReturn("id-2")
                .thenReturn("id-3")
                .thenReturn("id-4")
                .thenReturn("id-5");

        when(userRepository.save(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate key"));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> userService.createUser(
                        "John Doe",
                        "john@example.com",
                        "password123"
                )
        );

        assertEquals(
                "Unable to generate a unique user ID",
                exception.getMessage()
        );

        verify(idGenerator, times(5)).generate();
        verify(userRepository, times(5)).save(any(User.class));
    }

    @Test
    void findCredentialsByEmailShouldReturnCredentialsWhenUserExists() {
        User user = new User();
        user.setId("abc123");
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPasswordHashed("hashed-password");
        user.setRole(Role.ROLE_USER);

        when(stringService.normalizeString("john@example.com"))
                .thenReturn("john@example.com");
        when(userRepository.findByEmail("john@example.com"))
                .thenReturn(Optional.of(user));

        Optional<UserCredentials> result = userService.findCredentialsByEmail("john@example.com");

        assertTrue(result.isPresent());
        UserCredentials credentials = result.get();
        assertEquals("abc123", credentials.id());
        assertEquals("john@example.com", credentials.email());
        assertEquals(Role.ROLE_USER.name(), credentials.role());
        assertEquals("hashed-password", credentials.passwordHashed());

        verify(stringService).normalizeString("john@example.com");
        verify(userRepository).findByEmail("john@example.com");
    }

    @Test
    void findCredentialsByEmailShouldReturnEmptyWhenUserDoesNotExist() {
        when(stringService.normalizeString("john@example.com"))
                .thenReturn("john@example.com");
        when(userRepository.findByEmail("john@example.com"))
                .thenReturn(Optional.empty());

        Optional<UserCredentials> result = userService.findCredentialsByEmail("john@example.com");

        assertTrue(result.isEmpty());

        verify(stringService).normalizeString("john@example.com");
        verify(userRepository).findByEmail("john@example.com");
    }

    @Test
    void findCredentialsByEmailShouldNormalizeEmailBeforeLookup() {
        User user = new User();
        user.setId("abc123");
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPasswordHashed("hashed-password");
        user.setRole(Role.ROLE_USER);

        when(stringService.normalizeString("  JOHN@EXAMPLE.COM  "))
                .thenReturn("john@example.com");
        when(userRepository.findByEmail("john@example.com"))
                .thenReturn(Optional.of(user));

        Optional<UserCredentials> result = userService.findCredentialsByEmail("  JOHN@EXAMPLE.COM  ");

        assertTrue(result.isPresent());
        assertEquals("john@example.com", result.get().email());

        verify(stringService).normalizeString("  JOHN@EXAMPLE.COM  ");
        verify(userRepository).findByEmail("john@example.com");
    }

    @Test
    void findCredentialsByEmailShouldReturnEmptyWhenEmailIsNull() {
        Optional<UserCredentials> result = userService.findCredentialsByEmail(null);

        assertTrue(result.isEmpty());

        verifyNoInteractions(stringService, userRepository);
    }

    @Test
    void findCredentialsByEmailShouldReturnEmptyWhenEmailIsBlank() {
        Optional<UserCredentials> result = userService.findCredentialsByEmail("   ");

        assertTrue(result.isEmpty());

        verifyNoInteractions(stringService, userRepository);
    }

}
