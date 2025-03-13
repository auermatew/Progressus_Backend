package hu.progressus.controller;

import hu.progressus.dto.CreateUserDto;
import hu.progressus.dto.LoginDto;
import hu.progressus.response.AuthResponse;
import hu.progressus.service.AuthService;
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
  public ResponseEntity<AuthResponse> register(@Valid @RequestBody CreateUserDto dto, HttpServletResponse response) {
    return new ResponseEntity<>(authService.register(dto, response), HttpStatus.CREATED);
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginDto dto, HttpServletResponse response) {
    return ResponseEntity.ok(authService.login(dto, response));
  }

  @GetMapping("/authenticate")
  public ResponseEntity<AuthResponse> authenticate() {
    return ResponseEntity.ok(authService.authenticate());
  }
}