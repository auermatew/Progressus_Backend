package hu.progressus.entity;

import hu.progressus.enums.Role;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue
  private Long id;

  @Column
  @NotNull
  private String fullName;

  @NotNull
  private String email;

  @NotNull
  private String password;

  private String profilePicture;

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  @NotNull
  private LocalDate dateOfBirth;

  private String phoneNumber;

  private String description;

  @NotNull
  private Integer balance;

  @NotNull
  private Role role;

  @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private Teacher teacher;

  @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private BillingDetails billingDetails;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<UserInterest> userInterests;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<TeacherClassLesson> teacherClassLessons;

  @PrePersist
  private void onCreate(){
    if (this.balance == null){
      this.balance = 0;
    }
    if (this.userInterests == null){
      this.userInterests = new ArrayList<>();
    }
    if (this.teacherClassLessons == null){
      this.teacherClassLessons = new ArrayList<>();
    }
  }
}
