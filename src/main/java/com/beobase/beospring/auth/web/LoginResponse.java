package com.beobase.beospring.auth.web;

public record LoginResponse(boolean success, String token, String userId, String role) {
}
