package com.gayuh.personalproject.util;

import com.gayuh.personalproject.dto.UserObject;
import com.gayuh.personalproject.enumerated.ResponseMessage;
import com.gayuh.personalproject.enumerated.Role;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class FilterUtil {

    private FilterUtil() {
    }

    public static void filterAdmin(UserObject userObject) {
        if (!userObject.role().equals(Role.ROLE_ADMIN)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ResponseMessage.UNAUTHORIZED.value());
        }
    }

    public static void filterUser(UserObject userObject) {
        if (userObject.role().equals(Role.ROLE_GUEST)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ResponseMessage.UNAUTHORIZED.value());
        }
    }

}
