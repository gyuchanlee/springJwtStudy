package com.dodo.jwtreactspring.entity;

public enum Role {

    USER("USER"), ADMIN("ADMIN");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
