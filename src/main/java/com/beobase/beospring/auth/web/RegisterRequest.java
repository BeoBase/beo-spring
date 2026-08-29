package com.beobase.beospring.auth.web;

public record RegisterRequest(String name, String email, String password) {
}