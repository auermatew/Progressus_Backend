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
@Table(name = "user_interests",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id","subject_id"})},
    indexes = {
        @Index(name = "idx_user_interest_user_id", columnList = "user_id"),
        @Index(name = "idx_user_interest_subject_id", columnList = "subject_id"),
    })
public class UserInterest {
  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  private Subject subject;
}
