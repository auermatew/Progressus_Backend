package hu.progressus.service;

import hu.progressus.dto.CreateUserDto;
import hu.progressus.dto.LoginDto;
import hu.progressus.entity.User;
import hu.progressus.enums.Role;
import hu.progressus.repository.UserRepository;
import hu.progressus.response.AuthResponse;
import hu.progressus.response.TokenResponse;
import hu.progressus.util.UserUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.WebUtils;

import java.util.Optional;
/**
 * Handles all authentication-related operations:
 * registration, login, token issuance/refresh and logout.
 */
@RequiredArgsConstructor
@Service
public class AuthService {
  private final PasswordEncoder passwordEncoder;
  private final UserService userService;
  private final UserRepository userRepository;
  private final JwtService jwtService;
  private final UserUtils userUtils;

  /**
   * Builds an HttpOnly cookie containing a new refresh token for the given user.
   *
   * @param user the user to generate a refresh token for
   * @return a Cookie named "refreshToken" with the signed JWT
   */
  public Cookie refreshTokenCookie(User user){
    var refreshToken = jwtService.generateRefreshToken(user);
    Cookie cookie = new Cookie("refreshToken",refreshToken);
    cookie.setMaxAge(jwtService.getExpirationSeconds(refreshToken));
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    return cookie;
  }
  /**
   * Clears the refresh token cookie (sets it to null and max-age=0).
   *
   * @return an expired Cookie
   */
  public Cookie emptyRefreshTokenCookie(){
    Cookie cookie = new Cookie("refreshToken",null);
    cookie.setMaxAge(0);
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    return cookie;
  }

  /**
   * Registers a new user and immediately issues both access and refresh tokens.
   *
   * @param dto data required to create the user
   * @param response the HttpServletResponse to which the refresh-token cookie will be added
   * @return a TokenResponse containing the access token and its TTL
   * @throws ResponseStatusException if email already exists
   */
  public TokenResponse register(CreateUserDto dto, HttpServletResponse response){
    userService.ThrowUserEmailExists(dto.getEmail());
    User user = User.builder()
        .fullName(dto.getFullName())
        .email(dto.getEmail())
        .password(passwordEncoder.encode(dto.getPassword()))
        .dateOfBirth(dto.getDateOfBirth())
        .phoneNumber(dto.getPhoneNumber())
        .description(dto.getDescription())
        .role(Role.ROLE_STUDENT)
        .build();

    user = userRepository.save(user);

    response.addCookie(refreshTokenCookie(user));

    var token = jwtService.generateToken(user);
    return new TokenResponse(token, jwtService.getExpirationSeconds(token));
  }

  /**
   * Authenticates a user by email & password and issues tokens.
   *
   * @param dto contains login credentials
   * @param response the HttpServletResponse to add the refresh-token cookie
   * @return a TokenResponse with the access token and expiration
   * @throws ResponseStatusException if credentials are invalid
   */
  public TokenResponse login(LoginDto dto, HttpServletResponse response){
    Optional<User> optionalUser = userRepository.findUserByEmail(dto.getEmail());
    if (optionalUser.isEmpty()){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid email or password");
    }
    User user = optionalUser.get();

    if (!passwordEncoder.matches(dto.getPassword(),user.getPassword())){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid email or password");
    }

    response.addCookie(refreshTokenCookie(user));
    var token = jwtService.generateToken(user);
    return new TokenResponse(token, jwtService.getExpirationSeconds(token));
  }

  /**
   * Returns the currently authenticated user’s basic info.
   *
   * @return an AuthResponse DTO of the current user
   * @throws ResponseStatusException if no user is logged in
   */
  public AuthResponse authenticate(){
    User user = userUtils.currentUser();
    if(user == null ) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "you need to log in");
    return AuthResponse.of(user);
  }

  /**
   * Reads the refresh-token cookie, validates it, rotates it, and issues a new access token.
   *
   * @param request  the incoming HttpServletRequest (to read the cookie)
   * @param response the HttpServletResponse (to set the new refresh-token cookie)
   * @return a fresh TokenResponse with a new access token
   * @throws ResponseStatusException if the cookie is missing, invalid, or the user no longer exists
   */
  public TokenResponse refreshToken(HttpServletRequest request, HttpServletResponse response){
    Cookie refreshTokenCookie = WebUtils.getCookie(request, "refreshToken");

    if (refreshTokenCookie == null){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "no refresh token found");
    }
    Long userId = Long.valueOf(jwtService.extractUserId(refreshTokenCookie.getValue()));

    User user = userRepository.findById(userId).orElseThrow(() -> {
      response.addCookie(emptyRefreshTokenCookie());
      return new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found");
    });

    response.addCookie(refreshTokenCookie(user));

    var token = jwtService.generateToken(user);
    return new TokenResponse(token, jwtService.getExpirationSeconds(token));
  }

  /**
   * Logs out the current user by expiring the refresh-token cookie.
   *
   * @param response the HttpServletResponse to which the expired cookie is added
   */
  public void logout(HttpServletResponse response){
    response.addCookie(emptyRefreshTokenCookie());
  }
}
