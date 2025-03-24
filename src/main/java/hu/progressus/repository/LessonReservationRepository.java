package hu.progressus.repository;

import hu.progressus.entity.LessonReservation;
import hu.progressus.entity.TeacherClassLesson;
import hu.progressus.entity.User;
import hu.progressus.enums.LessonReservationStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LessonReservationRepository extends JpaRepository<LessonReservation, Long> {
  Optional<LessonReservation> findByUserAndTeacherClassLesson(User user, TeacherClassLesson teacherClassLesson);

  @Modifying
  @Transactional
  @Query("UPDATE LessonReservation lr SET lr.status = :declinedStatus WHERE lr.status = :pendingStatus")
  int declineAllPendingReservations(LessonReservationStatus pendingStatus, LessonReservationStatus declinedStatus);
}
