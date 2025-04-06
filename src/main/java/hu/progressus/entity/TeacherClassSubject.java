package hu.progressus.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "teacher_class_subjects",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"teacher_class_id","subject_id"})},
indexes = {
        @Index(name = "idx_teacher_class_subject_teacher_class_id", columnList = "teacher_class_id"),
        @Index(name = "idx_teacher_class_subject_subject_id", columnList = "subject_id"),
    })
public class TeacherClassSubject {
  @Id
  @GeneratedValue
  private Long id;


  @ManyToOne(fetch = FetchType.LAZY)
  private TeacherClass teacherClass;

  @ManyToOne(fetch = FetchType.LAZY)
  private Subject subject;
}
