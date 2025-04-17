package com.project.ecommerce_services.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

import javax.crypto.SecretKey;

@Component
public class JwtTokenProvider {
	
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);


    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

//    private final Key key;

//    public JwtTokenProvider() {
//    	this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
//    }
    
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
        		 .subject(username)
                 .issuedAt(now)
                 .expiration(expiryDate)
                 .signWith(key())
                 .compact();
                 }
  

    public String getUsernameFromJWT(String token) {
    			return Jwts.parser()
                        .verifyWith((SecretKey) key())
                .build().parseSignedClaims(token)
                .getPayload().getSubject();
    } 
 

    public boolean validateToken(String token) {
    	 try {
             System.out.println("Validate");
             Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(token);
             return true;
         } catch (MalformedJwtException e) {
             logger.error("Invalid JWT token: {}", e.getMessage());
         } catch (ExpiredJwtException e) {
             logger.error("JWT token is expired: {}", e.getMessage());
         } catch (UnsupportedJwtException e) {
             logger.error("JWT token is unsupported: {}", e.getMessage());
         } catch (IllegalArgumentException e) {
             logger.error("JWT claims string is empty: {}", e.getMessage());
         }
         return false;
    }
}