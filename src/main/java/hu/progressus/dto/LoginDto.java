package hu.progressus.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class LoginDto {

  @Email
  @NotNull
  private String email;

  @NotNull
  private String password;
}
