package com.beobase.beospring.shared;

public interface PasswordService {

    /**
     * Hash a raw password to store in DB or for security
     */
    String hash(String rawPassword);

    /**
     * Validate provided password against stored hash
     */
    boolean matches(String rawPassword, String hashedPassword);

}
