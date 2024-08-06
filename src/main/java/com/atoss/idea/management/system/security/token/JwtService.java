package com.atoss.idea.management.system.security.token;

import com.atoss.idea.management.system.repository.BlacklistedAccessTokenRepository;
import com.atoss.idea.management.system.repository.dto.UserSecurityDTO;
import com.atoss.idea.management.system.repository.entity.BlacklistedAccessToken;
import com.atoss.idea.management.system.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class JwtService {

    @Value("${aims.app.jwt.secret}")
    private String secret;

    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final BlacklistedAccessTokenRepository blacklistedAccessTokenRepository;

    @Getter
    private final AccessTokenConfig tokenConfig;

    /**
     * Extracts of any claim for JWT token
     * @param token The Jwt token from which to extract the claim
     * @param claimsResolver A function which takes the claims and returns the desired claims
     * @param <T> The type of the claim to extracted
     * @return Return the value of the claim
     *
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Gets the username from JWT token
     * @param token The JWT Token from which to extract the username
     * @return  Return the username from JWT token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Gets the expiration date from JWT token
     * @param token The JWT Token from which to extract the expiration date
     * @return  Return the expiration date from JWT token.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Gets all the claims from JWT token
     * @param token The JWT Token from which to extract all the claims
     * @return  Return claims from JWT token.
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Validates the JWT token
     * @param userDetails The user details
     * @param token The JWT Token to validate
     * @return  Return true if the token is valid and false otherwise
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token) && !isTokenBlacklisted(token));
    }

    /**
     * Generates a JWT token for an username
     * @param username The username for which to generate the token
     * @return Return generated Jwt token
     */
    public String generateToken(String username) {
        Map<String, Object> claims = objectMapper.convertValue(
                    userService.getUserByUsername(username, UserSecurityDTO.class),
                    HashMap.class);

        return createToken(claims, username);
    }

    /**
     * Checks if the JWT token is expired
     * @param token The Jwt token for checking
     * @return Return true if the token is expired and false otherwise
     */

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Boolean isTokenBlacklisted(String token) {
        return blacklistedAccessTokenRepository.findByToken(token).isPresent();
    }

    /**
     * Creates a Jwt token with the specified claims and username
     * @param claims The claims which are included into Jwt token
     * @param username The username which is included into Jwt TOKEN
     * @return Return the created jwt token
     */
    private String createToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + tokenConfig.getExpiryMs()))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Recovers the signing key used for jwt token
     * This method decodes the key from base64
     * @return Return the signing key used for jwt operations
     */
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    /**
     *
     * Invalidates an access token by blacklisting it.
     *
     * @param token The token to be blacklisted.
     * @see BlacklistedAccessToken
     * @see BlacklistedAccessTokenRepository
     *
     */
    public void invalidateToken(String token) {
        BlacklistedAccessToken blacklistedAccessToken = new BlacklistedAccessToken();
        blacklistedAccessToken.setToken(token);

        try {
            blacklistedAccessToken.setExpiry(extractExpiration(token));
        } catch (ExpiredJwtException e) {
            blacklistedAccessToken.setExpiry(e.getClaims().getExpiration());
        }

        blacklistedAccessTokenRepository.save(blacklistedAccessToken);
    }

    /**
     * Regularly deletes the blacklisted tokens from the database.
     * @see BlacklistedAccessToken
     * @see Scheduled
     */
    @Scheduled(cron = "0 0 12 * * ?")
    private void deleteExpiredTokens() {
        blacklistedAccessTokenRepository.deleteAllByExpiryLessThan(new Date());
    }
}
