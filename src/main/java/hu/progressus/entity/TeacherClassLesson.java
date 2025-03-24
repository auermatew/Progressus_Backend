package hu.progressus.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "teacher_class_lessons")
public class TeacherClassLesson {
  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @NotNull
  private TeacherClass teacherClass;

  @DateTimeFormat(pattern = "yyyy-MM-dd-HH-mm")
  @NotNull
  private LocalDateTime start_date;

  @DateTimeFormat(pattern = "yyyy-MM-dd-HH-mm")
  @NotNull
  private LocalDateTime end_date;

  /*@ManyToOne(fetch = FetchType.LAZY)
  private User user;*/

  @OneToMany(mappedBy = "teacherClassLesson", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<LessonReservation> lessonReservations;
}
