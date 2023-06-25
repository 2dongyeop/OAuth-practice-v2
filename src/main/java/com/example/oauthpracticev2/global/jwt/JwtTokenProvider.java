package com.example.oauthpracticev2.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    private final String secretKey = "example-secret-key";
    private final long ACCESS_TOKEN_EXPIRE_SECOND = 1200000;
    private final long REFRESH_TOKEN_EXPIRE_SECOND = 3600000;
//    private final RedisAdapter redisAdapter;


    public String createAccessToken(final String subject) {
        final String token = getToken(subject, ACCESS_TOKEN_EXPIRE_SECOND);

        log.info("생성된 accessToken: {}", token);
        return token;
    }


    public String createRefreshToken(final String subject) {
        final String token = getToken(subject, REFRESH_TOKEN_EXPIRE_SECOND);

        log.info("생성된 refreshToken: {}", token);
        return token;
    }

    private String getToken(final String subject, final long expiredSecond) {
        final Claims claims = Jwts.claims().setSubject(subject);

        final Date now = new Date();
        final Date validity = new Date(now.getTime() + expiredSecond);

        final String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
        return token;
    }
}
