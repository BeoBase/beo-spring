package com.beobase.beospring.shared;

import java.util.function.Predicate;

public interface TokenService {

    String generate(String userId, String email, String role);
    String extractUserId(String token);
    String extractEmail(String token);
    String extractRole(String token);
    boolean isValid(String token);
    String validateTokenAndGetUserId(String token, Predicate<String> userExists);
}
