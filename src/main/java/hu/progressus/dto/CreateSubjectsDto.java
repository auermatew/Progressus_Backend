package hu.progressus.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateSubjectsDto {
  @NotNull
  private List<String> subjectNames;
}
