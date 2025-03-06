package hu.progressus.response;

import hu.progressus.entity.Teacher;
import lombok.Data;

@Data
public class TeacherResponse {

  private String contactPhone;

  private String contactEmail;

  private UserResponse user;

  protected TeacherResponse (Teacher teacher){
    this.contactPhone = teacher.getContactPhone();
    this.contactEmail = teacher.getContactEmail();
    this.user = UserResponse.ofLite(teacher.getUser());
  }

  public static TeacherResponse of(Teacher teacher) {
    return new TeacherResponse(teacher);
  }
}