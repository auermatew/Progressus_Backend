package hu.progressus.service;

import hu.progressus.entity.User;
import io.jsonwebtoken.io.Decoders;
import org.springframework.beans.factory.annotation.Value;
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
  @Value("${jwt.secretKey}")
  private String secretKey;

  @Value("${jwt.access-token-expiration-ms}")
  private long accessTokenExpirationMs;

  @Value("${jwt.refresh-token-expiration-ms}")
  private long refreshTokenExpirationMs;

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
  public String generateToken(User user) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + accessTokenExpirationMs);
    return Jwts.builder()
        .subject(String.valueOf(user.getId()))
        .issuedAt(now)
        .expiration(expiry)
        .signWith(getSigningKey())
        .compact();
  }

  /**
   * Generate a long-lived refresh token.
   *
   * @param user the authenticated user
   * @return signed JWT string
   */
  public String generateRefreshToken(User user) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + refreshTokenExpirationMs);
    return Jwts.builder()
        .subject(String.valueOf(user.getId()))
        .issuedAt(now)
        .expiration(expiry)
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
  private Key getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}