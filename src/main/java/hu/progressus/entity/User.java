package hu.progressus.entity;

import hu.progressus.enums.ROLE;
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

import java.time.LocalDate;
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

  @NotNull
  private LocalDate dateOfBirth;

  private String phoneNumber;

  private String description;

  @NotNull
  private Integer balance;

  @NotNull
  private ROLE role;

  @OneToOne(mappedBy = "user")
  private Teacher teacher;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<UserInterest> userInterests;

  @PrePersist
  private void onCreate(){
    if (this.balance == null){
      this.balance = 0;
    }
  }
}
