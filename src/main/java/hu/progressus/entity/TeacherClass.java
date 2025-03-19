package hu.progressus.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
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

  @ManyToOne(fetch = FetchType.LAZY)
  @NotNull
  @JsonBackReference
  private Teacher teacher;

  @NotNull
  private String title;

  @NotNull
  private String description;

  @NotNull
  private Integer price;

  @OneToMany(mappedBy = "teacherClass", cascade = CascadeType.ALL)
  private List<TeacherClassSubject> subjects;

  @OneToMany(mappedBy = "teacherClass", cascade = CascadeType.ALL)
  private List<TeacherClassLesson> teacherClassLessons;

  @PrePersist
  private void onCreate() {
    if (this.subjects == null) {
      this.subjects = new ArrayList<>();
    }
    if (this.teacherClassLessons == null) {
      this.teacherClassLessons = new ArrayList<>();
    }
  }
}
