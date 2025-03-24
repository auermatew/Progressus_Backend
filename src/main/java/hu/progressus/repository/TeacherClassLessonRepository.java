package hu.progressus.repository;

import hu.progressus.entity.TeacherClassLesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TeacherClassLessonRepository extends JpaRepository<TeacherClassLesson, Long> {

  @Query("SELECT tcl " +
      "FROM TeacherClassLesson tcl " +
      "WHERE tcl.teacherClass.teacher.id = :teacherId " +
      "AND (" +
      "    (tcl.start_date < :endDate AND tcl.end_date > :startDate) " +
      ")")
  public Optional<TeacherClassLesson> findExistingLessonBetweenDates(@Param("teacherId") Long teacherId ,@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
