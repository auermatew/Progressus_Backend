package hu.progressus.dto;

import lombok.Data;
import java.util.List;

@Data
public class EditTeacherClassDto {

  private String title;
  private String description;
  private Integer price;

  private List<String> subjectsToAdd;
  private List<Long> subjectIdsToRemove;
}

