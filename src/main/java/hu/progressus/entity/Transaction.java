package hu.progressus.entity;

import hu.progressus.enums.TransactionStatus;
import jakarta.persistence.Entity;
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
@Table(name = "transactions")
public class Transaction {
  @Id
  @GeneratedValue
  private Long id;

  @OneToOne
  @NotNull
  private TeacherClassLesson teacherClassLesson;

  @ManyToOne
  @NotNull
  private BillingDetails billingDetails;

  @NotNull
  private Integer balance_before;

  @NotNull
  private Integer balance_after;

  @DateTimeFormat(pattern = "yyyy-MM-dd-HH-mm-ss")
  @NotNull
  private LocalDateTime date;

  @NotNull
  private TransactionStatus status;
}
