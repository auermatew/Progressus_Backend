package hu.progressus.response;

import hu.progressus.entity.TeacherClassLesson;
import hu.progressus.util.LessonUtils;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TeacherClassLessonResponse {

  private Long id;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private TeacherClassResponse teacherClass;
  private boolean isReserved;

  protected TeacherClassLessonResponse(TeacherClassLesson teacherClassLesson) {
    this.id = teacherClassLesson.getId();
    this.startDate = teacherClassLesson.getStart_date();
    this.endDate = teacherClassLesson.getEnd_date();
    this.teacherClass = TeacherClassResponse.of(teacherClassLesson.getTeacherClass());
    this.isReserved = LessonUtils.isLessonReserved(teacherClassLesson);
  }

  public static TeacherClassLessonResponse of(TeacherClassLesson teacherClassLesson){
    return new TeacherClassLessonResponse(teacherClassLesson);
  }

}
