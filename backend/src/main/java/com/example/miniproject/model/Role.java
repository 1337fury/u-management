package com.example.miniproject.model;

public enum Role {
    ADMIN,
    USER;

    public static Role fromString(String value) {
        try {
            return Role.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
