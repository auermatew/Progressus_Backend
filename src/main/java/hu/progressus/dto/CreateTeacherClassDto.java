package hu.progressus.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CreateTeacherClassDto {

  @NotBlank
  @Size(min=5,message = "Title must be at least 5 characters long")
  @Size(max=50,message = "Title must be at most 50 characters long")
  private String title;

  @NotBlank
  @Size(min=10,message = "Description must be at least 10 characters long")
  @Size(max=255,message = "Description must be at most 255 characters long")
  private String description;

  @NotNull
  @Min(value = 0, message = "Price must be at least 0")
  @Max(value = 1000000, message = "Price must be at most 1000000")
  private Integer price;

  @NotNull
  private List<String> subjects;
}
