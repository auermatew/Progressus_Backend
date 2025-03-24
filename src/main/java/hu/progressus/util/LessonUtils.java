package hu.progressus.util;


import hu.progressus.entity.TeacherClassLesson;
import hu.progressus.enums.LessonReservationStatus;

public class LessonUtils {

  public static boolean isLessonReserved(TeacherClassLesson lesson){
    return lesson.getLessonReservations().stream().anyMatch(reservation -> reservation.getStatus().equals(LessonReservationStatus.APPROVED));
  }
}
