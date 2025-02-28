package hu.progressus.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "teacher_classes")
public class TeacherClass {
  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne
  @NotNull
  private Teacher teacher;

  @NotNull
  private String title;

  @NotNull
  private String description;

  @NotNull
  private Integer price;

  @OneToMany(mappedBy = "teacherClass", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<TeacherClassSubject> subjects;

  @OneToMany(mappedBy = "teacherClass", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<TeacherClassLesson> teacherClassLessons;
}
