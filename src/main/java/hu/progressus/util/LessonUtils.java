package hu.progressus.util;


import hu.progressus.entity.TeacherClassLesson;
import hu.progressus.enums.LessonReservationStatus;

/**
 * Utility class for checking lesson reservations.
 */
public class LessonUtils {

  /**
   * Checks if a lesson is reserved.
   *
   * @param lesson the lesson to check
   * @return true if the lesson is reserved, false otherwise
   */
  public static boolean isLessonReserved(TeacherClassLesson lesson){
    return lesson.getLessonReservations().stream().anyMatch(reservation -> reservation.getStatus().equals(LessonReservationStatus.APPROVED));
  }
}
