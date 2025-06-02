package com.bikes.auth.domain;

public record TokenValidationResponse(String subject, String role) {}
