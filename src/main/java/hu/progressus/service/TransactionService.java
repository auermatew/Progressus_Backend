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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

/**
 * Service class for handling transactions related to lesson reservations.
 */
@Service
@RequiredArgsConstructor
public class TransactionService {
  private final TransactionRepository transactionRepository;
  private final LessonReservationRepository lessonReservationRepository;
  private final UserRepository userRepository;
  private final UserUtils userUtils;


  /**
   * Accepts a lesson transaction for a given reservation ID.
   *
   * @param reservationId the ID of the lesson reservation
   * @return a TransactionResponse containing transaction details
   * @throws ResponseStatusException if the reservation is not found, not approved, or already has a transaction
   */
  @Transactional
  public TransactionResponse acceptLessonTransaction(Long reservationId ){
    LessonReservation reservation = lessonReservationRepository.findById(reservationId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));

    if (reservation.getStatus() != LessonReservationStatus.APPROVED) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Reservation is not approved");
    }

    if (reservation.getTransaction() != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Transaction already exists");
    }

    User currentUser = userUtils.currentUser();
    User student = reservation.getUser();
    TeacherClassLesson lesson = reservation.getTeacherClassLesson();
    TeacherClass teacherClass = lesson.getTeacherClass();
    User teacher = teacherClass.getTeacher().getUser();
    Integer priceOfLesson = teacherClass.getPrice();

    if(!currentUser.getId().equals(student.getId())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not your reservation");

    if(currentUser.getBillingDetails() == null){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Billing details needed for transaction");
    }

    processTransfer(student,teacher,priceOfLesson);
    Transaction saveTransaction = saveTransaction(student,reservation);

    return TransactionResponse.of(saveTransaction);
  }

  /**
   * Processes the transfer of funds between two users.
   *
   * @param from the user sending the money
   * @param to the user receiving the money
   * @param amount the amount to be transferred
   * @throws ResponseStatusException if the sender has insufficient funds
   */
  @Transactional
  public void processTransfer(User from, User to, int amount){
    int updatedRows = userRepository.deductBalance(from.getId(), amount);
    if(updatedRows == 0){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient funds");
    }
    userRepository.creditBalance(to.getId(), amount);
  }

  /**
   * Saves a transaction for a given user and reservation.
   *
   * @param user the user making the transaction
   * @param reservation the lesson reservation associated with the transaction
   * @return the saved Transaction entity
   */
  @Transactional
  public Transaction saveTransaction(User user, LessonReservation reservation){
    Transaction transaction = Transaction.builder()
        .lessonReservation(reservation)
        .billingDetails(user.getBillingDetails())
        .date(LocalDateTime.now())
        .build();
    return transactionRepository.save(transaction);
  }

  /**
   * Retrieves all transactions associated with the current user's incoming lessons.
   *
   * @param pageable pagination information
   * @return a page of Transaction entities
   */
  public Page<Transaction> getIncomingTransactions(Pageable pageable){
    User user = userUtils.currentUser();
    return transactionRepository.findAllByLessonReservation_TeacherClassLesson_TeacherClass_Teacher_User_IdOrderByDateDesc(user.getId(), pageable);
  }

  /**
   * Retrieves all transactions associated with the current user's outgoing lessons.
   *
   * @param pageable pagination information
   * @return a page of Transaction entities
   */
  public Page<Transaction> getOutgoingTransactions(Pageable pageable){
    User user = userUtils.currentUser();
    return transactionRepository.findAllByBillingDetails_User_IdOrderByDateDesc(user.getId(), pageable);
  }

}
