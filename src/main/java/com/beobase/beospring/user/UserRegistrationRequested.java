package com.beobase.beospring.user;

public record UserRegistrationRequested(String name, String email, String password) {
}