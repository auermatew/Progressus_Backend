package hu.progressus.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateTeacherDto {

  @NotNull
  private String contactPhone;

  @NotNull
  private String contactEmail;
}
