package hu.progressus.service;

import hu.progressus.entity.LessonReservation;
import hu.progressus.entity.TeacherClass;
import hu.progressus.entity.TeacherClassLesson;
import hu.progressus.entity.User;
import hu.progressus.enums.LessonReservationStatus;
import hu.progressus.repository.LessonReservationRepository;
import hu.progressus.repository.TransactionRepository;
import hu.progressus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class TransactionService {
  private final TransactionRepository transactionRepository;
  private final LessonReservationRepository lessonReservationRepository;
  private final UserRepository userRepository;


  @Transactional
  public void processLessonTransaction(Long reservationId ){
    LessonReservation reservation = lessonReservationRepository.findById(reservationId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));

    if (reservation.getStatus() != LessonReservationStatus.APPROVED) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Reservation is not approved");
    }

    User student = reservation.getUser();
    TeacherClassLesson lesson = reservation.getTeacherClassLesson();
    TeacherClass teacherClass = lesson.getTeacherClass();
    User teacher = teacherClass.getTeacher().getUser();
    Integer priceOfLesson = teacherClass.getPrice();

    if (student.getBalance() < priceOfLesson) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient funds");
    }
  }


}
