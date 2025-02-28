package hu.progressus.controller;

import hu.progressus.dto.CreateUserDto;
import hu.progressus.entity.User;
import hu.progressus.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserController {
  private final UserService userService;
  @PostMapping("/registration")
  public User createUser(@RequestBody CreateUserDto dto){
    return userService.createUser(dto);
  }
}
