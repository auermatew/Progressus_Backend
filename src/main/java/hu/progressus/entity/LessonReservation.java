package hu.progressus.entity;

import hu.progressus.enums.LessonReservationStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "lesson_reservation",
    indexes = {
        @Index(name = "idx_lesson_reservation_teacher_class_lesson_id", columnList = "teacher_class_lesson_id"),
        @Index(name = "idx_lesson_reservation_user_id", columnList = "user_id"),
    })
public class LessonReservation {
  @GeneratedValue
  @Id
  private Long id;

  @NotNull
  private LessonReservationStatus status;

  private LocalDateTime createdAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @NotNull
  private TeacherClassLesson teacherClassLesson;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private User user;

  @OneToOne(mappedBy = "lessonReservation", cascade = CascadeType.ALL)
  private Transaction transaction;

}
