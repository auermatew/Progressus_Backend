package hu.progressus.dto;

import hu.progressus.enums.Role;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class CreateUserDto {
  private String fullName;

  private String email;

  private String password;

  private String profilePicture;

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate dateOfBirth;

  private String phoneNumber;

  private String description;

  private Role role;
}
