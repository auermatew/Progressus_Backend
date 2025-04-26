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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Operations around scheduling, reserving, approving and querying lessons
 * within teacher classes.
 */
@Service
@RequiredArgsConstructor
public class TeacherClassLessonService {
  private final UserUtils userUtils;
  private final TeacherClassRepository teacherClassRepository;
  private final TeacherClassLessonRepository teacherClassLessonRepository;
  private final LessonReservationRepository lessonReservationRepository;

  /**
   * Create a new lesson for the authenticated teacher.
   *
   * @param dto contains teacherClassId, startDate, endDate
   * @return a DTO of the newly created lesson
   * @throws ResponseStatusException if teacherClass not found or time conflict
   */
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

  /**
   * Reserve an available lesson for the current user (student).
   *
   * @param id the lesson slot ID
   * @throws ResponseStatusException on not found, own-lesson reservation, already reserved, or duplicate reservation
   */
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
        .createdAt(LocalDateTime.now())
        .build();
    lessonReservationRepository.save(reservation);
  }

  /**
   * Approve or decline a pending lesson reservation (teacher’s action).
   *
   * @param id the reservation ID
   * @param isApproved true to approve, false to decline
   * @throws ResponseStatusException on not found, unauthorized, already handled,
   *                                 or lesson now reserved
   */
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

  /**
   * Fetch all lessons for a teacher within a time window.
   *
   * @param teacherId the teacher’s user ID
   * @param startDate inclusive start of interval
   * @param endDate   inclusive end of interval
   * @return list of lesson DTOs
   */
  public List<TeacherClassLessonResponse> getAllLessonsForTeacherByDateInterval(Long teacherId, LocalDateTime startDate, LocalDateTime endDate){
  List<TeacherClassLesson> lessons =teacherClassLessonRepository.findTeacherClassLessons(teacherId,startDate,endDate);
  return lessons.stream()
      .map(TeacherClassLessonResponse::of)
      .toList();
  }

  /**
   * Fetch all lessons for a given class.
   *
   * @param teacherClassId the class ID
   * @return list of lesson DTOs
   */
  public List<TeacherClassLessonResponse> getAllLessonsForTeacherByClasses(Long teacherClassId){
    List<TeacherClassLesson> lessons = teacherClassLessonRepository.findTeacherClassLessonsByTeacherClass_Id(teacherClassId);
    return lessons.stream()
        .map(TeacherClassLessonResponse::of)
        .toList();
  }

  /**
   * Delete a lesson slot if unreserved and owned by current teacher.
   *
   * @param teacherClassLessonId the lesson ID
   * @throws ResponseStatusException on not found, unauthorized, or reserved
   */
  public void deleteLesson(Long teacherClassLessonId){
    User user = userUtils.currentUser();
    TeacherClassLesson teacherClassLesson = teacherClassLessonRepository.findById(teacherClassLessonId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found"));
    if (!user.getId().equals(teacherClassLesson.getTeacherClass().getTeacher().getUser().getId())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Teacher can only delete their own lessons.");
    }
    if(LessonUtils.isLessonReserved(teacherClassLesson)){
      throw new ResponseStatusException(HttpStatus.CONFLICT,"Cannot delete a lesson that has been reserved.");
    }
    teacherClassLessonRepository.delete(teacherClassLesson);
  }

  /**
   * Get all lessons across all classes for a teacher.
   *
   * @param teacherId the teacher user ID
   * @return list of lesson DTOs
   * @throws ResponseStatusException if teacher not found
   */
  public List<TeacherClassLessonResponse> getAllLessonsForTeacher(Long teacherId){
    if (!teacherClassRepository.existsByTeacherId(teacherId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher not found.");
    }
    List<TeacherClassLesson> lessons = teacherClassLessonRepository.findAllByTeacherClass_Teacher_Id(teacherId);
    return lessons.stream()
        .map(TeacherClassLessonResponse::of)
        .toList();
  }

  /**
   * Fetch a specific lesson slot for a teacher.
   *
   * @param teacherId the teacher user ID
   * @param lessonId the lesson slot ID
   * @return the lesson DTO
   * @throws ResponseStatusException if teacher or lesson missing
   */
  public TeacherClassLessonResponse getSpecificLessonForTeacher(Long teacherId, Long lessonId){
    if (!teacherClassRepository.existsByTeacherId(teacherId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher not found.");
    }
    TeacherClassLesson lesson = teacherClassLessonRepository
        .findByTeacherClass_Teacher_IdAndId(teacherId, lessonId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found for this teacher."));

    return TeacherClassLessonResponse.of(lesson);
  }

  /**
   * Retrieve all pending lesson reservations for the currently authenticated teacher.
   *
   * @param pageable the pagination information
   * @return a paginated list of pending lesson reservations
   */
  public Page<LessonReservation> getAllPendingLessonsForTeacher(Pageable pageable) {
    User user = userUtils.currentUser();
    return lessonReservationRepository.findAllByTeacherClassLesson_TeacherClass_Teacher_User_IdAndStatusOrderByCreatedAtDesc(
        user.getId(), LessonReservationStatus.PENDING, pageable);
  }
}
