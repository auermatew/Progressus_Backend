package hu.progressus.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateSubjectsDto {
  private List<String> subjectNames;
}
