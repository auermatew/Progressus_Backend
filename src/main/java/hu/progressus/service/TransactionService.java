package hu.progressus.service;

import hu.progressus.entity.LessonReservation;
import hu.progressus.entity.TeacherClass;
import hu.progressus.entity.TeacherClassLesson;
import hu.progressus.entity.Transaction;
import hu.progressus.entity.User;
import hu.progressus.enums.LessonReservationStatus;
import hu.progressus.repository.LessonReservationRepository;
import hu.progressus.repository.TransactionRepository;
import hu.progressus.repository.UserRepository;
import hu.progressus.response.TransactionResponse;
import hu.progressus.util.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionService {
  private final TransactionRepository transactionRepository;
  private final LessonReservationRepository lessonReservationRepository;
  private final UserRepository userRepository;
  private final UserUtils userUtils;


  @Transactional
  public TransactionResponse acceptLessonTransaction(Long reservationId ){
    LessonReservation reservation = lessonReservationRepository.findById(reservationId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));

    if (reservation.getStatus() != LessonReservationStatus.APPROVED) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Reservation is not approved");
    }

    User currentUser = userUtils.currentUser();
    User student = reservation.getUser();
    TeacherClassLesson lesson = reservation.getTeacherClassLesson();
    TeacherClass teacherClass = lesson.getTeacherClass();
    User teacher = teacherClass.getTeacher().getUser();
    Integer priceOfLesson = teacherClass.getPrice();

    if(!currentUser.getId().equals(student.getId())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not your reservation");

    processTransfer(student,teacher,priceOfLesson);
    Transaction saveTransaction = saveTransaction(student,reservation);

    return TransactionResponse.of(saveTransaction);
  }

  @Transactional
  public void processTransfer(User from, User to, int amount){
    int updatedRows = userRepository.deductBalance(from.getId(), amount);
    if(updatedRows == 0){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient funds");
    }
    userRepository.creditBalance(to.getId(), amount);
  }

  @Transactional
  public Transaction saveTransaction(User user, LessonReservation reservation){
    Transaction transaction = Transaction.builder()
        .lessonReservation(reservation)
        .billingDetails(user.getBillingDetails())
        .date(LocalDateTime.now())
        .build();
    return transactionRepository.save(transaction);
  }
}
