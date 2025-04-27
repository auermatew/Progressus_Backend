package hu.progressus.response;

import hu.progressus.entity.Teacher;
import hu.progressus.util.SubjectUtils;
import lombok.Data;

import java.util.List;

@Data
public class TeacherResponse {
  private Long id;

  private String contactPhone;

  private String contactEmail;

  private UserResponse user;

  private List<SubjectResponse> subjects;

  protected TeacherResponse (Teacher teacher){
    this.id = teacher.getId();
    this.contactPhone = teacher.getContactPhone();
    this.contactEmail = teacher.getContactEmail();
    this.user = UserResponse.ofLite(teacher.getUser());
    this.subjects = SubjectUtils.getSubjectsOfTeacher(teacher).stream().map(SubjectResponse::of).toList();
  }

  public static TeacherResponse of(Teacher teacher) {
    return new TeacherResponse(teacher);
  }
}