package hu.progressus.service;

import hu.progressus.dto.CreateUserDto;
import hu.progressus.dto.LoginDto;
import hu.progressus.entity.User;
import hu.progressus.enums.Role;
import hu.progressus.repository.UserRepository;
import hu.progressus.response.AuthResponse;
import hu.progressus.util.UserUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuthService {
  private final PasswordEncoder passwordEncoder;
  private final UserService userService;
  private final UserRepository userRepository;
  private final JwtService jwtService;
  private final UserUtils userUtils;

  public Cookie tokenCookie(User user){
    var token = JwtService.generateToken(user);
    Cookie cookie = new Cookie("token",token);
    cookie.setMaxAge(jwtService.getExpirationSeconds(token));
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    return cookie;
  }

  public Cookie refreshTokenCookie(User user){
    var refreshToken = JwtService.generateRefreshToken(user);
    Cookie cookie = new Cookie("refreshToken",refreshToken);
    cookie.setMaxAge(jwtService.getExpirationSeconds(refreshToken));
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    return cookie;
  }


  public AuthResponse register(CreateUserDto dto, HttpServletResponse response){
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

    response.addCookie(tokenCookie(user));
    response.addCookie(refreshTokenCookie(user));

    return AuthResponse.of(user);
  }

  public AuthResponse login(LoginDto dto, HttpServletResponse response){
    Optional<User> optionalUser = userRepository.findUserByEmail(dto.getEmail());
    if (optionalUser.isEmpty()){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid email or password");
    }
    User user = optionalUser.get();

    if (!passwordEncoder.matches(dto.getPassword(),user.getPassword())){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid email or password");
    }

    response.addCookie(tokenCookie(user));
    response.addCookie(refreshTokenCookie(user));

    return AuthResponse.of(user);
  }

  public AuthResponse authenticate(){
    User user = userUtils.currentUser();
    if(user == null ) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "you need to log in");
    return AuthResponse.of(user);
  }
}
