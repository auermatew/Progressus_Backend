package hu.progressus.dto;

import hu.progressus.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class CreateUserDto {
  @NotNull
  private String fullName;

  @NotNull
  @Email
  private String email;

  @NotNull
  private String password;

  private String profilePicture;

  @NotNull
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate dateOfBirth;

  private String phoneNumber;

  private String description;

  @NotNull
  private Role role;
}
