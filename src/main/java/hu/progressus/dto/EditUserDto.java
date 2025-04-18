package hu.progressus.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class EditUserDto {

  @Size(min = 5, max = 50)
  private String fullName;

  @Size(min = 8, max = 255)
  private String password;

  @Pattern(regexp = "^\\+((?:9[679]|8[035789]|6[789]|5[90]|42|3[578]|2[1-689])|9[0-58]|8[1246]|6[0-6]|5[1-8]|4[013-9]|3[0-469]|2[70]|7|1)(?:\\W*\\d){0,13}\\d$")
  private String phoneNumber;

  @Size(max = 255)
  private String description;
}
