package com.example.oauthpracticev2.global.jwt;

import com.example.oauthpracticev2.global.redis.RedisAdapter;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class JwtTokenProvider {

    private final String secretKey;
    private final long ACCESS_TOKEN_EXPIRE_SECOND;
    private final long REFRESH_TOKEN_EXPIRE_SECOND;
    private final RedisAdapter redisAdapter;


    public JwtTokenProvider(
            @Value("${security.jwt.token.secret-key}") final String secretKey,
            @Value("${security.jwt.token.access-expire-length}") final long ACCESS_TOKEN_EXPIRE_SECOND,
            @Value("${security.jwt.token.refresh-expire-length}") final long REFRESH_TOKEN_EXPIRE_SECOND,
            final RedisAdapter redisAdapter) {
        this.secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        this.ACCESS_TOKEN_EXPIRE_SECOND = ACCESS_TOKEN_EXPIRE_SECOND;
        this.REFRESH_TOKEN_EXPIRE_SECOND = REFRESH_TOKEN_EXPIRE_SECOND;
        this.redisAdapter = redisAdapter;
    }

    public String createAccessToken(final String subject) {
        final String token = getToken(subject, ACCESS_TOKEN_EXPIRE_SECOND);

        log.info("생성된 accessToken: {}", token);
        return token;
    }


    public String createRefreshToken(final String subject) {
        final String token = getToken(subject, REFRESH_TOKEN_EXPIRE_SECOND);

        //생성된 refresh 토큰을 redis에 저장
        redisAdapter.setValue(subject, token, REFRESH_TOKEN_EXPIRE_SECOND, TimeUnit.SECONDS);

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


    //TODO : 추후에 토큰이 필요한 API를 호출할 경우, 아래 메소드를 통해 토큰 유효성 검증을 하면 된다.
    public boolean validateToken(final String email, final String accessToken) throws IllegalAccessException {

        /** 유효하지 않은 토큰일 경우 */
        isValidToken(email);

        /** 로그아웃 처리된 토큰으로 요청할 경우 */
        validIsAlreadyLogout(email);

        try {
            final Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(accessToken);

            /** 만료시간이 지났을 경우 */
            isExpiredToken(claims);
            return true;
        } catch (JwtException | IllegalArgumentException | IllegalAccessException e) {
            /* do nothing! */
            //TODO : 제대로 예외처리 해주기
            return false;
        }
    }

    private void isValidToken(final String email) throws IllegalAccessException {

        if (!redisAdapter.hasKey(email)) {
            //TODO : custom exception 만들기
            throw new IllegalAccessException();
        }
    }


    private void isExpiredToken(final Jws<Claims> claims) throws IllegalAccessException {

        if (claims.getBody().getExpiration().before(new Date())) {
            //TODO : custom exception 만들기
            throw new IllegalAccessException();
        }
    }


    private void validIsAlreadyLogout(final String email) throws IllegalAccessException {

        final String value = redisAdapter.getValue(email);
        System.out.println("value = " + value);

        if (value.equals("LOGOUT_STATUS")) {
            //TODO : custom exception 만들기
            throw new IllegalAccessException();
        }
    }
}
