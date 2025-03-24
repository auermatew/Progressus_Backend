package hu.progressus.entity;

import hu.progressus.enums.LessonReservationStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "lesson_reservation")
public class LessonReservation {
  @GeneratedValue
  @Id
  private Long id;

  @NotNull
  private LessonReservationStatus status;

  @ManyToOne(fetch = FetchType.LAZY)
  @NotNull
  private TeacherClassLesson teacherClassLesson;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private User user;
}
