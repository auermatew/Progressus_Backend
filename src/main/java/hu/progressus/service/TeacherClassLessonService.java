package hu.progressus.service;

import hu.progressus.dto.CreateTeacherClassLessonDto;
import hu.progressus.entity.LessonReservation;
import hu.progressus.entity.TeacherClass;
import hu.progressus.entity.TeacherClassLesson;
import hu.progressus.entity.Transaction;
import hu.progressus.entity.User;
import hu.progressus.enums.LessonReservationStatus;
import hu.progressus.repository.LessonReservationRepository;
import hu.progressus.repository.TeacherClassLessonRepository;
import hu.progressus.repository.TeacherClassRepository;
import hu.progressus.response.TeacherClassLessonResponse;
import hu.progressus.response.TeacherClassResponse;
import hu.progressus.util.LessonUtils;
import hu.progressus.util.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeacherClassLessonService {
  private final UserUtils userUtils;
  private final TeacherClassRepository teacherClassRepository;
  private final TeacherClassLessonRepository teacherClassLessonRepository;
  private final LessonReservationRepository lessonReservationRepository;

  @Transactional
  public TeacherClassLessonResponse createTeacherClassLesson(CreateTeacherClassLessonDto dto){
    User user = userUtils.currentUser();
    TeacherClass teacherClass  =  teacherClassRepository.findTeacherClassByIdAndTeacher_User_Id(dto.getTeacherClassId(), user.getId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher Class not found"));
    Optional<TeacherClassLesson> optionalTeacherClassLesson = teacherClassLessonRepository.findExistingLessonBetweenDates(user.getId(),dto.getStartDate(),dto.getEndDate());
    if(optionalTeacherClassLesson.isPresent()){
      throw new ResponseStatusException(HttpStatus.CONFLICT, "A lesson is already present in the given time.");
    }
    TeacherClassLesson teacherClassLesson = TeacherClassLesson.builder()
        .teacherClass(teacherClass)
        .start_date(dto.getStartDate())
        .end_date(dto.getEndDate())
        .build();
    teacherClassLessonRepository.save(teacherClassLesson);
    return TeacherClassLessonResponse.of(teacherClassLesson);
  }

  @Transactional
  public void reserveLesson(Long id) {
    User user = userUtils.currentUser();
    TeacherClassLesson teacherClassLesson = teacherClassLessonRepository.findById(id).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found."));
    if(teacherClassLesson.getTeacherClass().getTeacher().getUser().getId().equals(user.getId())){
      throw new ResponseStatusException(HttpStatus.CONFLICT,"Cannot reserve your own lesson.");
    }
    if (LessonUtils.isLessonReserved(teacherClassLesson)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Lesson is already reserved.");
    }
    if(lessonReservationRepository.findByUserAndTeacherClassLesson(user,teacherClassLesson).isPresent()) {
    throw new ResponseStatusException(HttpStatus.CONFLICT, "This user has already submitted a reservation.");
    }
    LessonReservation reservation = LessonReservation.builder()
        .teacherClassLesson(teacherClassLesson)
        .user(user)
        .status(LessonReservationStatus.PENDING)
        .build();
    lessonReservationRepository.save(reservation);
  }

  @Transactional
  public void handleReservationStatus(Long id, boolean isApproved){
    User user = userUtils.currentUser();
    LessonReservation reservation = lessonReservationRepository.findById(id).orElseThrow(
        ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found."));
    if(!reservation.getTeacherClassLesson().getTeacherClass().getTeacher().getUser().getId().equals(user.getId())){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
    if (LessonUtils.isLessonReserved(reservation.getTeacherClassLesson())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Lesson is already reserved.");
    }
    if (reservation.getStatus() != LessonReservationStatus.PENDING){
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Reservation is already handled.");
    }
    if (isApproved){
      reservation.setStatus(LessonReservationStatus.APPROVED);
      lessonReservationRepository.save(reservation);
      lessonReservationRepository.declineAllPendingReservations(LessonReservationStatus.PENDING,LessonReservationStatus.DECLINED);
    }
    else {
      reservation.setStatus(LessonReservationStatus.DECLINED);
      lessonReservationRepository.save(reservation);
    }
  }


// TODO: Implement method to get all lessons for one teacher
// TODO: Implement method to get lessons for one teacher by date
// TODO: Implement method to get a specific lesson for one teacher
// TODO: Implement method to get all lessons for one teacher by classes
// TODO: Implement method to delete a lesson

}
