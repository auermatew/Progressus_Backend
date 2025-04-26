package hu.progressus.servicetests;

import hu.progressus.entity.*;
import hu.progressus.enums.LessonReservationStatus;
import hu.progressus.repository.LessonReservationRepository;
import hu.progressus.repository.TeacherClassLessonRepository;
import hu.progressus.repository.TeacherClassRepository;
import hu.progressus.service.TeacherClassLessonService;
import hu.progressus.util.UserUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeacherClassLessonServiceTest {
  @Mock
  UserUtils userUtils;
  @Mock
  TeacherClassRepository teacherClassRepository;
  @Mock
  TeacherClassLessonRepository teacherClassLessonRepository;
  @Mock
  LessonReservationRepository lessonReservationRepository;

  @InjectMocks
  TeacherClassLessonService teacherClassLessonService;

  User user;
  User secondaryUser;
  Teacher teacher;
  TeacherClass teacherClass;
  TeacherClassLesson teacherClassLesson;
  LessonReservation lessonReservation;

  @BeforeEach
  void setup() {
    user = User.builder()
        .id(1L)
        .build();
    secondaryUser = User.builder()
        .id(2L)
        .build();
    teacher = Teacher.builder()
        .id(1L)
        .user(user)
        .build();
    user.setTeacher(teacher);
    teacherClass = TeacherClass.builder()
        .id(1L)
        .teacher(teacher)
        .build();
    teacherClassLesson = TeacherClassLesson.builder()
        .id(1L)
        .teacherClass(teacherClass)
        .lessonReservations(new ArrayList<>())
        .build();

    lessonReservation = LessonReservation.builder()
        .id(1L)
        .teacherClassLesson(teacherClassLesson)
        .status(LessonReservationStatus.PENDING)
        .user(user)
        .build();

    teacherClassLesson.setLessonReservations(List.of(lessonReservation));

  }

  @Test
  void whenReservationDoesNotExist_ThrowsResponseStatusException() {
    lenient().when(userUtils.currentUser()).thenReturn(user);
    lenient().when(lessonReservationRepository.findById(1L)).thenReturn(Optional.empty());

    var result = assertThrows(ResponseStatusException.class, () ->
      teacherClassLessonService.handleReservationStatus(1L,true));

    assertEquals(HttpStatus.NOT_FOUND,result.getStatusCode());
  }

  @Test
  void whenLessonAlreadyReserved_ThrowsResponseStatusException() {
    lessonReservation.setStatus(LessonReservationStatus.APPROVED);
    lenient().when(lessonReservationRepository.findById(1L)).thenReturn(Optional.ofNullable(lessonReservation));
    lenient().when(userUtils.currentUser()).thenReturn(user);


    var result = assertThrows(ResponseStatusException.class, ()->
      teacherClassLessonService.handleReservationStatus(1L, true));
    assertEquals(HttpStatus.CONFLICT,result.getStatusCode());
  }

  @Test
  void whenCurrentUserIsNotOwnerOfLesson_ThrowsResponseStatusException() {
    lenient().when(userUtils.currentUser()).thenReturn(secondaryUser);
    lenient().when(lessonReservationRepository.findById(1L)).thenReturn(Optional.ofNullable(lessonReservation));

    var result = assertThrows(ResponseStatusException.class, ()-> teacherClassLessonService.handleReservationStatus(1L, true));
    assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
  }

  @Test
  void whenAcceptingReservation_DeclineAllPendingReservations() {
    lenient().when(userUtils.currentUser()).thenReturn(user);
    lenient().when(lessonReservationRepository.findById(1L)).thenReturn(Optional.ofNullable(lessonReservation));

    teacherClassLessonService.handleReservationStatus(1L, true);

    verify(lessonReservationRepository).save(any());
    verify(lessonReservationRepository).declineAllPendingReservations(any(),any());
  }

  @Test
  void declinePendingReservation() {
    lenient().when(userUtils.currentUser()).thenReturn(user);
    lenient().when(lessonReservationRepository.findById(1L)).thenReturn(Optional.ofNullable(lessonReservation));

    teacherClassLessonService.handleReservationStatus(1L, false);

    verify(lessonReservationRepository).save(any());
    verify(lessonReservationRepository,times(0)).declineAllPendingReservations(any(),any());
  }

  @Test
  void whenReservationGivenStatusNotPending_ThrowsResponseStatusException(){
    lessonReservation.setStatus(LessonReservationStatus.DECLINED);
    lenient().when(userUtils.currentUser()).thenReturn(user);
    lenient().when(lessonReservationRepository.findById(1L)).thenReturn(Optional.ofNullable(lessonReservation));

    var result = assertThrows(ResponseStatusException.class, () -> teacherClassLessonService.handleReservationStatus(1L,false));

    assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
  }

  //region deleteLesson() tests
  @Test
  void whenLessonNotFound_ThrowsResponseStatusException() {
    lenient().when(userUtils.currentUser()).thenReturn(user);
    lenient().when(teacherClassLessonRepository.findById(1L)).thenReturn(Optional.empty());

    var result = assertThrows(ResponseStatusException.class, () ->
        teacherClassLessonService.deleteLesson(1L));

    assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
  }

  @Test
  void whenCurrentUserIsNotOwnerOfDeleteLesson_ThrowsResponseStatusException() {
    lenient().when(userUtils.currentUser()).thenReturn(secondaryUser);
    lenient().when(teacherClassLessonRepository.findById(1L))
        .thenReturn(Optional.of(teacherClassLesson));

    var result = assertThrows(ResponseStatusException.class, () ->
        teacherClassLessonService.deleteLesson(1L));

    assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
  }

  @Test
  void whenLessonIsReserved_ThrowsResponseStatusException() {
    lessonReservation.setStatus(LessonReservationStatus.APPROVED);
    teacherClassLesson.setLessonReservations(List.of(lessonReservation));

    lenient().when(userUtils.currentUser()).thenReturn(user);
    lenient().when(teacherClassLessonRepository.findById(1L))
        .thenReturn(Optional.of(teacherClassLesson));

    var result = assertThrows(ResponseStatusException.class, () ->
        teacherClassLessonService.deleteLesson(1L));

    assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
  }

  @Test
  void whenUnreservedLessonAndOwner_DeletesLesson() {
    teacherClassLesson.setLessonReservations(new ArrayList<>());

    lenient().when(userUtils.currentUser()).thenReturn(user);
    lenient().when(teacherClassLessonRepository.findById(1L))
        .thenReturn(Optional.of(teacherClassLesson));

    teacherClassLessonService.deleteLesson(1L);

    verify(teacherClassLessonRepository).delete(teacherClassLesson);
  }
  //endregion
}
