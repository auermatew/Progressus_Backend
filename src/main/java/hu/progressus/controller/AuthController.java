package hu.progressus.controller;

import hu.progressus.dto.CreateUserDto;
import hu.progressus.dto.LoginDto;
import hu.progressus.response.AuthResponse;
import hu.progressus.response.TokenResponse;
import hu.progressus.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth")
public class AuthController {
  private final AuthService authService;

  @PostMapping("/registration")
  @Operation(summary = "Register a new user", description = "Register a new user.")
  public ResponseEntity<TokenResponse> register(@Valid @RequestBody CreateUserDto dto, HttpServletResponse response) {
    return new ResponseEntity<>(authService.register(dto, response), HttpStatus.CREATED);
  }

  @PostMapping("/login")
  @Operation(summary = "Login", description = "Login to the application.")
  public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginDto dto, HttpServletResponse response) {
    return ResponseEntity.ok(authService.login(dto, response));
  }

  @GetMapping("/authenticate")
  @Operation(summary = "Authenticate", description = "Authenticate the user, returns the user details.")
  public ResponseEntity<AuthResponse> authenticate() {
    return ResponseEntity.ok(authService.authenticate());
  }

  @GetMapping("/refresh")
  @Operation(summary = "Refresh token", description = "Refresh the access token using the refresh token.")
  public ResponseEntity<TokenResponse> refresh(HttpServletRequest request, HttpServletResponse response) {
    return ResponseEntity.ok(authService.refreshToken(request, response));
  }

  @GetMapping("/logout")
  @Operation(summary = "Logout", description = "Logout the user, invalidates the refresh token.")
  public void logout(HttpServletResponse response){
    authService.logout(response);
  }
}