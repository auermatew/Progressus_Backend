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

@Service
public class JwtService {
  private static final String SECRET_KEY = "bWVtb3J5c3VjaHNsb3BlbW9kZWx3aGVuZXZlcmZlbmNlbm90ZWRvb3JiYWxhbmNlY2x1Yndyb25nc3VjY2Vzc2Z1bHN3aW1hdGJyZWF0aGJvYXRmcm9tcHJvZ3JhbXdvcmtsaWtlbHl0aGVzZXJhaW5veHlnZW5kZWVwcG9lbXRoaXJkZG9jdG9yY29tbWFuZG5pY2VkaW5uZXJib25lYXNrY29tcG9zZWRmZWxsY2FuYWx1cG9uZHJvcHRpdGxlZnJpZW5kbHlhY3RpdmVjb2F0Y2hhbmNldHdlbnR5cmVtZW1iZXJ0aHJlYWRidXN5ZmxpZXNjcmVhbWVudGVyZg==";

  public int getExpirationSeconds(String token) {
    return (int)((extractClaim(token, Claims::getExpiration).getTime() -  new Date().getTime()) / 1000);
  }

  public String extractUserId(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  public static String generateToken(User user) {
    return Jwts
        .builder()
        .subject(String.valueOf(user.getId()))
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis()+ (1000 * 60) * 60))
        .signWith(getSigningKey())
        .compact();
  }

  public static String generateRefreshToken(User user) {
    return Jwts
        .builder()
        .subject(String.valueOf(user.getId()))
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis()+ (1000L * 60L) * 60L * 24L * 30L))
        .signWith(getSigningKey())
        .compact();
  }

  public Claims extractAllClaims(String token) {
    return Jwts.parser()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  public boolean isTokenValid(String token, User user) {
    final String id = extractUserId(token);
    return (id.equals(String.valueOf(user.getId())) && !isTokenExpired(token));
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private static Key getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}