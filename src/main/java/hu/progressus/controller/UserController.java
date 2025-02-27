package hu.progressus.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("api/v1/user")
public class UserController {
  @GetMapping
  public String getTest(){
    return "test";
  }
}
