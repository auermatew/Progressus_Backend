package hu.progressus.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
@Table(name = "subjects",
indexes = {
    @Index(name = "idx_subject_name", columnList = "subject"),
})
public class Subject {
  @Id
  @GeneratedValue
  private Long id;

  @Column(unique = true)
  private String subject;

  private boolean isVerified;

  @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL)
  private List<TeacherSubjectTag> teacherSubjectTags;

  @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL)
  private List<UserInterest> userInterests;

  @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL)
  private List<TeacherClassSubject> teacherClassSubjects;
}
