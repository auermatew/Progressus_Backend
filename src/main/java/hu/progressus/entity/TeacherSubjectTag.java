package hu.progressus.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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
@Table(name = "teacher_subjects",uniqueConstraints = {@UniqueConstraint(columnNames = {"teacher_id","subject_id"})})
public class TeacherSubjectTag {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne
  private Teacher teacher;

  @ManyToOne
  private Subject subject;
}
