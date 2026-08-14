package com.beobase.beospring.shared;

public interface TokenService {

    String generate(String userId);
    String extractUserId(String token);
    boolean isValid(String token);
}
