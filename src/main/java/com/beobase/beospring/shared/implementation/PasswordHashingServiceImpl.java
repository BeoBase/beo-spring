package com.beobase.beospring.shared.implementation;

import com.beobase.beospring.shared.PasswordService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
class PasswordHashingServiceImpl implements PasswordService {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * Hash a raw password using BCrypt
     */
    @Override
    public String hash(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    /**
     * Validate provided password against stored hash
     */
    @Override
    public boolean matches(String rawPassword, String hashedPassword) {
        return encoder.matches(rawPassword, hashedPassword);
    }
}
