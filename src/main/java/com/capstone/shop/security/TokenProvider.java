package com.capstone.shop.security;
import com.capstone.shop.config.AppProperties;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.util.Date;
@Service
public class TokenProvider {
    @Value("${jwt.secret}")
    private String secret;
    private static final Logger logger = LoggerFactory.getLogger(TokenProvider.class);

    public String createToken(Authentication authentication, Date expiryDate) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        // 사용자의 ID를 가져와서 토큰에담는다.
        Long userId = userPrincipal.getId();

        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("role",userPrincipal.getAuthorities())
                .signWith(SignatureAlgorithm.HS512, secret)
                .setExpiration(expiryDate) // 만료 시간 설정
                .compact();
    }

    public String createRefreshToken(Authentication authentication, Date refreshExpiry) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long userId = userPrincipal.getId();
        return Jwts.builder()
                .setSubject(userId.toString())
                .signWith(SignatureAlgorithm.HS512, secret)
                .setExpiration(refreshExpiry) // 만료 시간 설정
                .compact();
    }

    public Claims getExpiredTokenClaims(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            logger.info("Expired JWT token.");
            return e.getClaims();
        }
        return null;
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty.");
        }
        return false;
    }
}