package hu.progressus.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

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

  @ManyToOne
  @NotNull
  private TeacherClass teacherClass;

  @DateTimeFormat(pattern = "yyyy-MM-dd-HH-mm")
  @NotNull
  private LocalDateTime start_date;

  @DateTimeFormat(pattern = "yyyy-MM-dd-HH-mm")
  @NotNull
  private LocalDateTime end_date;

  @ManyToOne
  private User user;

  @OneToOne(mappedBy = "teacherClassLesson", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private Transaction transaction;
}
