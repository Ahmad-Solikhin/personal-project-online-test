package com.gayuh.personalproject.util;

import com.gayuh.personalproject.dto.UserDetails;
import com.gayuh.personalproject.enumerated.ResponseMessage;
import com.gayuh.personalproject.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
public class ExtractHeaderUtil {
    private final JwtService jwtService = new JwtService();

    public static UserDetails extractHeader(HttpServletRequest servletRequest, String secretKey) {
        ExtractHeaderUtil extractHeaderUtil = new ExtractHeaderUtil();
        return extractHeaderUtil.extract(servletRequest, secretKey);
    }

    private UserDetails extract(HttpServletRequest servletRequest, String secretKey) {
        final String jwtToken;
        final String authHeader = servletRequest.getHeader("Authorization");


        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ResponseMessage.UNAUTHORIZED.value());
        }

        jwtToken = authHeader.substring(7);

        log.info("Token value : {}", jwtToken);

        return jwtService.extractUserFromJwtToken(jwtToken, secretKey);
    }
}
