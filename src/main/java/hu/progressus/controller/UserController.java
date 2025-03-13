package hu.progressus.controller;

import hu.progressus.dto.EditUserDto;
import hu.progressus.response.AuthResponse;
import hu.progressus.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserController {
  private final UserService userService;

  @PatchMapping("/edit")
  public ResponseEntity<AuthResponse> editUser(@RequestBody @Valid EditUserDto dto){
    return ResponseEntity.ok(userService.editUser(dto));
  }
}
