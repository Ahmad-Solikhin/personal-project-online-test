package com.gayuh.personalproject.util;

import com.gayuh.personalproject.enumerated.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ResponseStatusExceptionUtil {

    private ResponseStatusExceptionUtil(){}

    public static void unauthorizedVoid(){
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ResponseMessage.UNAUTHORIZED.value());
    }
    public static ResponseStatusException unauthorized(){
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, ResponseMessage.UNAUTHORIZED.value());
    }

    public static void notFoundVoid(){
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseMessage.DATA_NOT_FOUND.value());
    }
    public static ResponseStatusException notFound(){
        return new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseMessage.DATA_NOT_FOUND.value());
    }
}
