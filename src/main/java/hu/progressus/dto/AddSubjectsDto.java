package hu.progressus.dto;

import lombok.Data;

import java.util.List;

@Data
public class AddSubjectsDto {
  private List<String> subjectNames;
}
