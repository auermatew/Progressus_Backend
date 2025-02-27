package hu.progressus.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
@Table(name = "teachers")
public class Teacher {
  @Id
  @GeneratedValue
  private Long id;

  @OneToOne
  private User user;

  private String contactPhone;

  private String contactEmail;

  @OneToMany(mappedBy = "teacher",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<TeacherSubjectTag> subjects;

  @OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<TeacherClass> teacherClasses;

}
