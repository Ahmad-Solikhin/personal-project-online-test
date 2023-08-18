package com.gayuh.personalproject.enumerated;

import java.util.Arrays;
import java.util.List;

public enum Role {
    ROLE_ADMIN("admin"),
    ROLE_USER("user"),
    ROLE_GUEST("guest");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }

    public static Role getValueOf(String roleValue) {

        List<Role> roles = Arrays.asList(Role.values());

        return roles.stream().filter(role -> role.value.equals(roleValue)).findFirst().orElseThrow(
                () -> new IllegalArgumentException("No matching constant for [" + roleValue + "]")
        );
    }
}
