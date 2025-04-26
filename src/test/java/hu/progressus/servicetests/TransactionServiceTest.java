package hu.progressus.servicetests;

import hu.progressus.entity.*;
import hu.progressus.enums.LessonReservationStatus;
import hu.progressus.repository.LessonReservationRepository;
import hu.progressus.repository.TransactionRepository;
import hu.progressus.repository.UserRepository;
import hu.progressus.response.TransactionResponse;
import hu.progressus.service.TransactionService;
import hu.progressus.util.UserUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
  @Mock TransactionRepository transactionRepository;
  @Mock LessonReservationRepository lessonReservationRepository;
  @Mock UserRepository userRepository;
  @Mock UserUtils userUtils;

  @InjectMocks TransactionService transactionService;

  User student;
  User teacher;
  BillingDetails billingDetails;
  TeacherClass teacherClass;
  TeacherClassLesson lesson;
  LessonReservation reservation;

  @BeforeEach
  void setup() {
    // student setup
    student = User.builder().id(1L).build();
    billingDetails = new BillingDetails();
    student.setBillingDetails(billingDetails);

    // teacher setup
    teacher = User.builder().id(2L).build();
    var teacherEntity = Teacher.builder().id(10L).user(teacher).build();
    teacher.setTeacher(teacherEntity);

    // teacherClass + lesson
    teacherClass = TeacherClass.builder()
        .id(20L)
        .teacher(teacherEntity)
        .price(50)
        .subjects(new ArrayList<>())
        .build();
    teacherClass.setSubjects(new ArrayList<>());
    lesson = TeacherClassLesson.builder()
        .id(30L)
        .teacherClass(teacherClass)
        .build();
    lesson.setLessonReservations(new ArrayList<>());

    // reservation setup
    reservation = LessonReservation.builder()
        .id(40L)
        .teacherClassLesson(lesson)
        .user(student)
        .status(LessonReservationStatus.APPROVED)
        .build();
    // by default reservation.getTransaction() is null
  }

  //region acceptLessonTransaction() tests
  @Test
  void whenReservationNotFound_acceptLessonTransaction_throwsNotFound() {
    when(lessonReservationRepository.findById(1L)).thenReturn(Optional.empty());

    var ex = assertThrows(ResponseStatusException.class, () ->
        transactionService.acceptLessonTransaction(1L));

    assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    assertEquals("Reservation not found", ex.getReason());
  }

  @Test
  void whenReservationNotApproved_acceptLessonTransaction_throwsConflict() {
    reservation.setStatus(LessonReservationStatus.PENDING);
    when(lessonReservationRepository.findById(reservation.getId()))
        .thenReturn(Optional.of(reservation));

    var ex = assertThrows(ResponseStatusException.class, () ->
        transactionService.acceptLessonTransaction(reservation.getId()));

    assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    assertEquals("Reservation is not approved", ex.getReason());
  }

  @Test
  void whenTransactionAlreadyExists_acceptLessonTransaction_throwsConflict() {
    reservation.setTransaction(new Transaction());
    when(lessonReservationRepository.findById(reservation.getId()))
        .thenReturn(Optional.of(reservation));

    var ex = assertThrows(ResponseStatusException.class, () ->
        transactionService.acceptLessonTransaction(reservation.getId()));

    assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    assertEquals("Transaction already exists", ex.getReason());
  }

  @Test
  void whenNotReservationOwner_acceptLessonTransaction_throwsBadRequest() {
    User other = User.builder().id(99L).build();
    when(lessonReservationRepository.findById(reservation.getId()))
        .thenReturn(Optional.of(reservation));
    when(userUtils.currentUser()).thenReturn(other);

    var ex = assertThrows(ResponseStatusException.class, () ->
        transactionService.acceptLessonTransaction(reservation.getId()));

    assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    assertEquals("Not your reservation", ex.getReason());
  }

  @Test
  void whenNoBillingDetails_acceptLessonTransaction_throwsBadRequest() {
    reservation.getUser().setBillingDetails(null);
    when(lessonReservationRepository.findById(reservation.getId()))
        .thenReturn(Optional.of(reservation));
    when(userUtils.currentUser()).thenReturn(student);

    var ex = assertThrows(ResponseStatusException.class, () ->
        transactionService.acceptLessonTransaction(reservation.getId()));

    assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    assertEquals("Billing details needed for transaction", ex.getReason());
  }

  @Test
  void whenInsufficientFunds_acceptLessonTransaction_throwsBadRequest() {
    when(lessonReservationRepository.findById(reservation.getId()))
        .thenReturn(Optional.of(reservation));
    when(userUtils.currentUser()).thenReturn(student);
    when(userRepository.deductBalance(student.getId(), teacherClass.getPrice()))
        .thenReturn(0);

    var ex = assertThrows(ResponseStatusException.class, () ->
        transactionService.acceptLessonTransaction(reservation.getId()));

    assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    assertEquals("Insufficient funds", ex.getReason());
  }

  @Test
  void whenValidReservation_acceptLessonTransaction_returnsTransactionResponse() {
    when(lessonReservationRepository.findById(reservation.getId()))
        .thenReturn(Optional.of(reservation));
    when(userUtils.currentUser()).thenReturn(student);
    when(userRepository.deductBalance(student.getId(), teacherClass.getPrice()))
        .thenReturn(1);
    Transaction savedTx = Transaction.builder()
        .lessonReservation(reservation)
        .billingDetails(billingDetails)
        .date(LocalDateTime.of(2025,4,26,12,0))
        .build();
    savedTx.setId(500L);
    when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTx);

    TransactionResponse resp = transactionService.acceptLessonTransaction(reservation.getId());

    verify(userRepository).deductBalance(student.getId(), teacherClass.getPrice());
    verify(userRepository).creditBalance(teacher.getId(), teacherClass.getPrice());
    verify(transactionRepository).save(any(Transaction.class));
    assertEquals(500L, resp.getId());
    assertEquals(savedTx.getDate(), resp.getDate());
  }
  //endregion

  //region processTransfer() tests
  @Test
  void whenInsufficientFunds_processTransfer_throwsBadRequest() {
    when(userRepository.deductBalance(student.getId(), 20)).thenReturn(0);

    var ex = assertThrows(ResponseStatusException.class, () ->
        transactionService.processTransfer(student, teacher, 20));

    assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    assertEquals("Insufficient funds", ex.getReason());
  }

  @Test
  void whenSufficientFunds_processTransfer_callsCreditAndDebit() {
    when(userRepository.deductBalance(student.getId(), 30)).thenReturn(1);

    transactionService.processTransfer(student, teacher, 30);

    verify(userRepository).deductBalance(student.getId(), 30);
    verify(userRepository).creditBalance(teacher.getId(), 30);
  }
  //endregion

  //region saveTransaction() tests
  @Test
  void whenSavingTransaction_saveTransaction_returnsSavedEntity() {
    Transaction returned = new Transaction();
    returned.setId(600L);
    when(transactionRepository.save(any(Transaction.class))).thenReturn(returned);

    Transaction result = transactionService.saveTransaction(student, reservation);

    verify(transactionRepository).save(any(Transaction.class));
    assertEquals(returned, result);
  }
  //endregion

  //region getIncomingTransactions() tests
  @Test
  void whenIncomingRequested_getIncomingTransactions_returnsPagedTransactions() {
    Pageable pageable = PageRequest.of(0,5);
    Page<Transaction> page = new PageImpl<>(List.of(new Transaction()));
    when(userUtils.currentUser()).thenReturn(teacher);
    when(transactionRepository
        .findAllByLessonReservation_TeacherClassLesson_TeacherClass_Teacher_User_IdOrderByDateDesc(
            teacher.getId(), pageable))
        .thenReturn(page);

    Page<Transaction> result = transactionService.getIncomingTransactions(pageable);

    assertEquals(page, result);
  }
  //endregion

  //region getOutgoingTransactions() tests
  @Test
  void whenOutgoingRequested_getOutgoingTransactions_returnsPagedTransactions() {
    Pageable pageable = PageRequest.of(0,5);
    Page<Transaction> page = new PageImpl<>(List.of(new Transaction()));
    when(userUtils.currentUser()).thenReturn(student);
    when(transactionRepository
        .findAllByBillingDetails_User_IdOrderByDateDesc(student.getId(), pageable))
        .thenReturn(page);

    Page<Transaction> result = transactionService.getOutgoingTransactions(pageable);

    assertEquals(page, result);
  }
  //endregion
}
