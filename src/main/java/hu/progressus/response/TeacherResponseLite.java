package hu.progressus.response;

import hu.progressus.entity.Teacher;
import lombok.Data;

@Data
public class TeacherResponseLite {
  private String contactPhone;
  private String contactEmail;

  protected TeacherResponseLite(Teacher teacher) {
    this.contactPhone = teacher.getContactPhone();
    this.contactEmail = teacher.getContactEmail();
  }

  public static TeacherResponseLite of(Teacher teacher) {
    return new TeacherResponseLite(teacher);
  }
}

