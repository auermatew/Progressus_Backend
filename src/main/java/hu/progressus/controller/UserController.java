package hu.progressus.controller;

import hu.progressus.dto.EditUserDto;
import hu.progressus.entity.User;
import hu.progressus.response.AuthResponse;
import hu.progressus.response.UserResponse;
import hu.progressus.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserController {
  private final UserService userService;

  @GetMapping("/{userId}")
  public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId){
    return ResponseEntity.ok(userService.getUserById(userId));
  }

  @GetMapping("/all")
  public ResponseEntity<Page<UserResponse>> getAllUser(@PageableDefault(size = 15)
                                                          Pageable pageable){
    Page<User> userPage = userService.getAllUsers(pageable);

    Page<UserResponse> userResponses = userPage.map(UserResponse::ofLite);
    return ResponseEntity.ok(userResponses);
  }

  @PatchMapping("/edit")
  public ResponseEntity<AuthResponse> editUser(@RequestBody @Valid EditUserDto dto){
    return ResponseEntity.ok(userService.editUser(dto));
  }
}
