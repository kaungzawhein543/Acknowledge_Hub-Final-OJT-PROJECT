package com.ace.enums;


public enum DefaultPassword {
    USER_PASSWORD("acknowledgeHub"),
    ADMIN_PASSWORD("adminPassword");

    private final String password;

    DefaultPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return this.password;
    }
}
