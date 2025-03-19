package hu.progressus.response;

import hu.progressus.entity.TeacherClass;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class TeacherClassResponse {

  private Long id;

  @NotNull
  private String title;

  @NotNull
  private String description;

  @NotNull
  private Integer price;

  @NotNull
  private List<String> subjects;

  protected TeacherClassResponse(TeacherClass teacherClass) {
    this.id = teacherClass.getId();
    this.title = teacherClass.getTitle();
    this.description = teacherClass.getDescription();
    this.price = teacherClass.getPrice();
    this.subjects =  teacherClass.getSubjects().stream().map(tcs -> tcs.getSubject().getSubject()).collect(Collectors.toList());
  }

  public static TeacherClassResponse of(TeacherClass teacherClass) {
    return new TeacherClassResponse(teacherClass);
  }
}
