package hu.progressus.response;

import hu.progressus.entity.LessonReservation;
import hu.progressus.enums.LessonReservationStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LessonReservationResponse {
  private Long id;
  private LessonReservationStatus status;
  private UserResponse user;
  private TeacherClassLessonResponse teacherClassLesson;
  private LocalDateTime createdAt;

  protected LessonReservationResponse(LessonReservation lessonReservation) {
    this.id = lessonReservation.getId();
    this.status = lessonReservation.getStatus();
    this.user = UserResponse.ofLite(lessonReservation.getUser());
    this.teacherClassLesson = TeacherClassLessonResponse.of(lessonReservation.getTeacherClassLesson());
    this.createdAt = lessonReservation.getCreatedAt();
  }

  public static LessonReservationResponse of(LessonReservation lessonReservation){
    return new LessonReservationResponse(lessonReservation);
  }
}
