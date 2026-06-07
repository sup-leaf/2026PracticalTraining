package com.bjtumarket.util;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
    }

    @Test
    void generateToken_shouldReturnValidToken() {
        String token = jwtUtil.generateToken(1L, 1);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void parseToken_shouldExtractUserIdAndUserType() {
        String token = jwtUtil.generateToken(42L, 2);
        Claims claims = jwtUtil.parseToken(token);
        assertEquals(42, claims.get("userId", Integer.class));
        assertEquals(2, claims.get("userType", Integer.class));
    }

    @Test
    void parseToken_shouldContainIssuedAtAndExpiration() {
        String token = jwtUtil.generateToken(1L, 1);
        Claims claims = jwtUtil.parseToken(token);
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        assertTrue(claims.getExpiration().after(claims.getIssuedAt()));
    }

    @Test
    void validateToken_shouldReturnTrueForValidToken() {
        String token = jwtUtil.generateToken(1L, 1);
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    void validateToken_shouldReturnFalseForInvalidToken() {
        assertFalse(jwtUtil.validateToken("invalid.token.here"));
        assertFalse(jwtUtil.validateToken(""));
        assertFalse(jwtUtil.validateToken(null));
    }

    @Test
    void validateToken_shouldReturnFalseForTamperedToken() {
        String token = jwtUtil.generateToken(1L, 1);
        String tampered = token + "tampered";
        assertFalse(jwtUtil.validateToken(tampered));
    }

    @Test
    void generateToken_containsUserIdInClaims() {
        String token = jwtUtil.generateToken(99L, 3);
        Claims claims = jwtUtil.parseToken(token);
        assertEquals(99, claims.get("userId", Integer.class));
        assertEquals(3, claims.get("userType", Integer.class));
    }
}
