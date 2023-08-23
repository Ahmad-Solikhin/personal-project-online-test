package com.gayuh.personalproject.service;

import com.gayuh.personalproject.dto.UserDetails;
import com.gayuh.personalproject.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class JwtService {
    @Value("${SIGNATURE_KEY}")
    private String secretKey;

    public String generateToken(User user) {
        return Jwts.builder()
                .setClaims(Map.of(
                        "role", user.getRole().getName(),
                        "id", user.getId()
                ))
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(2)))
                .signWith(getSigninKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public UserDetails extractUserFromJwtToken(String jwtToken, String secretKey) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigninKey(secretKey))
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody();

            return new UserDetails(
                    claims.get("id", String.class),
                    claims.getSubject(),
                    claims.get("role", String.class)
            );
        } catch (SignatureException | MalformedJwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token not valid");
        } catch (ExpiredJwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token expired");
        } catch (UnsupportedJwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token not supported");
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Signature key is null");
        }
    }

    private Key getSigninKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Key getSigninKey(String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
