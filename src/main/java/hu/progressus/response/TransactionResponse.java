package hu.progressus.response;

import hu.progressus.entity.Transaction;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class TransactionResponse {
  private Long id;

  private BillingDetailsResponse billingDetails;

  @DateTimeFormat(pattern = "yyyy-MM-dd-HH-mm-ss")
  private LocalDateTime date;

  private TeacherClassLessonResponse lesson;

  protected  TransactionResponse(Transaction transaction){
    this.id = transaction.getId();
    //TODO: implement billing details CRUD
    this.billingDetails = BillingDetailsResponse.of(transaction.getBillingDetails());
    this.date = transaction.getDate();
    this.lesson = TeacherClassLessonResponse.of(transaction.getLessonReservation().getTeacherClassLesson());
  }

  public static TransactionResponse of(Transaction transaction){
    return new TransactionResponse(transaction);
  }

}
