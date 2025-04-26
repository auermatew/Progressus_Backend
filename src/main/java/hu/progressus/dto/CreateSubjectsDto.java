package hu.progressus.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CreateSubjectsDto {
  @NotNull
  @Size(min=1,message = "At least one subject must be added")
  private List<String> subjectNames;
}
