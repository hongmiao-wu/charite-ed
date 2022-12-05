package com.wise23.chariteed.model;

public enum Role {
    USER("User"),
    //    MODERATOR("Moderator"),
    ADMIN("Admin");
    private final String value;

    private Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

