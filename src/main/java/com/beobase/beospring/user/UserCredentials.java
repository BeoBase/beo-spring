package com.beobase.beospring.user;

/**
 * Public boundary record exposing only the data the auth module needs to
 * verify login credentials, without leaking the internal User entity.
 */
public record UserCredentials(String id, String email, String role, String passwordHashed) {
}
