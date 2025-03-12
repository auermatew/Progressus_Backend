package hu.progressus.response;

import hu.progressus.entity.TeacherClass;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TeacherClassResponse {

  private Long id;

  @NotNull
  private String title;

  @NotNull
  private String description;

  @NotNull
  private Integer price;

  protected TeacherClassResponse(TeacherClass teacherClass) {
    this.id = teacherClass.getId();
    this.title = teacherClass.getTitle();
    this.description = teacherClass.getDescription();
    this.price = teacherClass.getPrice();
  }

  public static TeacherClassResponse of(TeacherClass teacherClass) {
    return new TeacherClassResponse(teacherClass);
  }
}
