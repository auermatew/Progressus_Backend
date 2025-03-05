package hu.progressus.controller;

import hu.progressus.response.AuthResponse;
import hu.progressus.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserController {
  private final UserService userService;

  @PostMapping("/edit")
  public ResponseEntity<AuthResponse> editProfile(HttpServletRequest request){
    return null;
        //ResponseEntity.ok(userService.edit(request));
  }
}
