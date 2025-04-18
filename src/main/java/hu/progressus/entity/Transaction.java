package hu.progressus.entity;

import hu.progressus.enums.LessonReservationStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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
@Table(name = "transactions",
    indexes = {
        @Index(name = "idx_transaction_billing_details_id", columnList = "billing_details_id"),
    })
public class Transaction {
  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @NotNull
  private BillingDetails billingDetails;

  @DateTimeFormat(pattern = "yyyy-MM-dd-HH-mm-ss")
  @NotNull
  private LocalDateTime date;

  @OneToOne
  private LessonReservation lessonReservation;
}
