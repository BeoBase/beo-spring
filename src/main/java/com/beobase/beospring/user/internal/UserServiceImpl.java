package com.beobase.beospring.user.internal;

import java.util.Optional;

import com.beobase.beospring.shared.IdGenerator;
import com.beobase.beospring.shared.PasswordService;
import com.beobase.beospring.shared.StringService;
import com.beobase.beospring.user.UserCredentials;
import com.beobase.beospring.user.UserInfo;
import com.beobase.beospring.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {

    private static final int MAX_ID_GENERATION_ATTEMPTS = 5;

    private final UserRepository userRepository;
    private final StringService stringService;
    private final PasswordService passwordService;
    private final IdGenerator idGenerator;

    @Override
    public UserInfo findById(String id) {
        User user = userRepository.findById(id).orElseThrow();

        return new UserInfo(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    @Override
    public Optional<UserCredentials> findCredentialsByEmail(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }

        String normalizedEmail = stringService.normalizeString(email);

        return userRepository.findByEmail(normalizedEmail)
                .map(user -> new UserCredentials(
                        user.getId(),
                        user.getEmail(),
                        user.getRole().name(),
                        user.getPasswordHashed()
                ));
    }

    @Override
    public UserInfo createUser(String name, String email, String password) {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or empty or blank");
        }
        String normalizedEmail = stringService.normalizeString(email);
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new IllegalArgumentException("Email already exists: " + normalizedEmail);
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or empty or blank");
        }
        String hashedPassword = passwordService.hash(password);

        for (int attempt = 1; attempt <= MAX_ID_GENERATION_ATTEMPTS; attempt++) {
            try {
                String id = idGenerator.generate();

                User user = new User();
                user.setId(id);
                user.setName(name);
                user.setEmail(normalizedEmail);
                user.setPasswordHashed(hashedPassword);
                user.setRole(Role.ROLE_USER);

                User savedUser = userRepository.save(user);

                return new UserInfo(
                        savedUser.getId(),
                        savedUser.getName(),
                        savedUser.getEmail(),
                        savedUser.getRole().name()
                );

            } catch (DataIntegrityViolationException e) {
                if (attempt == MAX_ID_GENERATION_ATTEMPTS) {
                    throw new IllegalStateException(
                            "Unable to generate a unique user ID",
                            e
                    );
                }
            }
        }

        throw new IllegalStateException("Unable to create user");
    }

}
