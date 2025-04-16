package hu.progressus.service;

import hu.progressus.entity.User;
import io.jsonwebtoken.io.Decoders;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

/**
 * Utility for creating, parsing, and validating JWT access & refresh tokens.
 */
@Service
public class JwtService {
  private static final String SECRET_KEY = "bWVtb3J5c3VjaHNsb3BlbW9kZWx3aGVuZXZlcmZlbmNlbm90ZWRvb3JiYWxhbmNlY2x1Yndyb25nc3VjY2Vzc2Z1bHN3aW1hdGJyZWF0aGJvYXRmcm9tcHJvZ3JhbXdvcmtsaWtlbHl0aGVzZXJhaW5veHlnZW5kZWVwcG9lbXRoaXJkZG9jdG9yY29tbWFuZG5pY2VkaW5uZXJib25lYXNrY29tcG9zZWRmZWxsY2FuYWx1cG9uZHJvcHRpdGxlZnJpZW5kbHlhY3RpdmVjb2F0Y2hhbmNldHdlbnR5cmVtZW1iZXJ0aHJlYWRidXN5ZmxpZXNjcmVhbWVudGVyZg==";

  /**
   * Compute time-to-live of a token in seconds.
   *
   * @param token the JWT string
   * @return seconds until expiration
   */
  public int getExpirationSeconds(String token) {
    return (int)((extractClaim(token, Claims::getExpiration).getTime() -  new Date().getTime()) / 1000);
  }

  /**
   * Extract the subject (user ID) from the token.
   *
   * @param token the JWT string
   * @return the subject claim
   */
  public String extractUserId(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /**
   * Generic extractor for any claim.
   *
   * @param token the JWT
   * @param claimsResolver function to pick specific claim
   * @param <T> return type
   * @return the extracted claim
   */
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  /**
   * Generate a short-lived access token.
   *
   * @param user the authenticated user
   * @return signed JWT string
   */
  public static String generateToken(User user) {
    return Jwts
        .builder()
        .subject(String.valueOf(user.getId()))
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis()+ (1000 * 60) * 60))
        .signWith(getSigningKey())
        .compact();
  }

  /**
   * Generate a long-lived refresh token.
   *
   * @param user the authenticated user
   * @return signed JWT string
   */
  public static String generateRefreshToken(User user) {
    return Jwts
        .builder()
        .subject(String.valueOf(user.getId()))
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis()+ (1000L * 60L) * 60L * 24L * 30L))
        .signWith(getSigningKey())
        .compact();
  }

  /**
   * Parse all claims from a JWT.
   *
   * @param token the signed JWT string
   * @return the Claims object
   */
  public Claims extractAllClaims(String token) {
    return Jwts.parser()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  /**
   * Validate that token belongs to given user and is not expired.
   *
   * @param token the JWT string
   * @param user  the user to check against
   * @return true if valid
   */
  public boolean isTokenValid(String token, User user) {
    final String id = extractUserId(token);
    return (id.equals(String.valueOf(user.getId())) && !isTokenExpired(token));
  }

  /**
   * Check whether the token has expired.
   *
   * @param token the JWT string
   * @return true if expiration is before now
   */
  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  /**
   * Get the expiration date of the token.
   *
   * @param token the JWT string
   * @return the expiration Date
   */
  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  /**
   * Decode the base64 secret and produce the signing key.
   *
   * @return an HMAC-SHA key
   */
  private static Key getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}