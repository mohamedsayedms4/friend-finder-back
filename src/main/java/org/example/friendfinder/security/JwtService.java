package org.example.friendfinder.security;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

/**
 * JWT creation and parsing service.
 *
 * Uses separate secrets for access and refresh tokens.
 *
 * @author Mohamed Sayed
 */
@Service
public class JwtService {

    @Value("${app.security.jwt.issuer}")
    private String issuer;

    @Value("${app.security.jwt.access.secret}")
    private String accessSecret;

    @Value("${app.security.jwt.refresh.secret}")
    private String refreshSecret;

    @Value("${app.security.jwt.access.expiration-minutes}")
    private long accessExpMinutes;

    @Value("${app.security.jwt.refresh.expiration-days}")
    private long refreshExpDays;

    /**
     * Generates an access token.
     *
     * @param subject principal identifier (email)
     * @param claims additional claims (role, uid, etc.)
     * @return signed JWT
     */
    public String generateAccessToken(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(accessExpMinutes * 60L);

        return Jwts.builder()
                .setIssuer(issuer)
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .addClaims(claims)
                .signWith(accessKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Generates a refresh token.
     *
     * @param subject principal identifier (email)
     * @param claims additional claims
     * @return signed JWT
     */
    public String generateRefreshToken(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(refreshExpDays * 24L * 60L * 60L);

        return Jwts.builder()
                .setIssuer(issuer)
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .addClaims(claims)
                .signWith(refreshKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Parses and validates an access token.
     *
     * @param token jwt string
     * @return JWS claims
     */
    public Jws<Claims> parseAccessToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(accessKey())
                .requireIssuer(issuer)
                .build()
                .parseClaimsJws(token);
    }

    /**
     * Parses and validates a refresh token.
     *
     * @param token jwt string
     * @return JWS claims
     */
    public Jws<Claims> parseRefreshToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(refreshKey())
                .requireIssuer(issuer)
                .build()
                .parseClaimsJws(token);
    }

    public long accessExpiresInSec() {
        return accessExpMinutes * 60L;
    }

    public long refreshExpiresInSec() {
        return refreshExpDays * 24L * 60L * 60L;
    }

    private Key accessKey() {
        return Keys.hmacShaKeyFor(accessSecret.getBytes(StandardCharsets.UTF_8));
    }

    private Key refreshKey() {
        return Keys.hmacShaKeyFor(refreshSecret.getBytes(StandardCharsets.UTF_8));
    }
}
