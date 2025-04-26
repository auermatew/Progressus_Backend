package hu.progressus.controller;

import hu.progressus.dto.CreateTeacherClassLessonDto;
import hu.progressus.entity.LessonReservation;
import hu.progressus.response.LessonReservationResponse;
import hu.progressus.response.TeacherClassLessonResponse;
import hu.progressus.service.TeacherClassLessonService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/v1/teacher-class-lessons")
@RequiredArgsConstructor
public class TeacherClassLessonController {
  private final TeacherClassLessonService teacherClassLessonService;

  @PreAuthorize("hasRole('TEACHER')")
  @PostMapping("/create")
  @Operation(summary = "Create a lesson", description = "Create a lesson. The user must be a teacher to access this endpoint.")
  public ResponseEntity<TeacherClassLessonResponse> createLesson(@Valid @RequestBody CreateTeacherClassLessonDto dto){
    return ResponseEntity.ok(teacherClassLessonService.createTeacherClassLesson(dto));
  }

  @PreAuthorize("hasRole('STUDENT')")
  @PostMapping("/reserve/{lessonId}")
  @Operation(summary = "Reserve a lesson", description = "Reserve a lesson. The user must be a student to access this endpoint.")
  public void reserveLesson(@PathVariable Long lessonId){
    teacherClassLessonService.reserveLesson(lessonId);
  }

  @GetMapping("/teacher/{teacherId}")
  @Operation(summary = "Get all lessons of a teacher", description = "Get all lessons of a teacher.")
  public ResponseEntity<List<TeacherClassLessonResponse>> getAllLessonsForTeacher(@PathVariable Long teacherId){
    return ResponseEntity.ok(teacherClassLessonService.getAllLessonsForTeacher(teacherId));
  }

  @GetMapping("/teacher/{teacherId}/lesson/{lessonId}")
  @Operation(summary = "Get a specific lesson of a teacher", description = "Get a specific lesson of a teacher by teacher ID and lesson ID.")
  public ResponseEntity<TeacherClassLessonResponse> getSpecificLessonForTeacher(@PathVariable Long teacherId, @PathVariable Long lessonId){
    return ResponseEntity.ok(teacherClassLessonService.getSpecificLessonForTeacher(teacherId, lessonId));
  }

  @PreAuthorize("hasRole('TEACHER')")
  @PostMapping("/reservation/{id}/{accepted}")
  @Operation(summary = "Handle reservation status", description = "Handle reservation status. The user must be a teacher to access this endpoint.")
  public void handleReservation(@PathVariable Long id, @PathVariable boolean accepted){
    teacherClassLessonService.handleReservationStatus(id,accepted);
  }

  @GetMapping("/lessons-by/{teacherClassId}")
  @Operation(summary = "Get all lessons of a teacher class", description = "Get all lessons of a teacher class by teacher class ID.")
  public ResponseEntity<List<TeacherClassLessonResponse>> getAllLessonsForTeacherByClasses(@PathVariable Long teacherClassId){
    return ResponseEntity.ok(teacherClassLessonService.getAllLessonsForTeacherByClasses(teacherClassId));
  }

  @GetMapping("/calendar")
  @Operation(summary = "Get all lessons of a teacher by date interval", description = "Get all lessons of a teacher by date interval.")
  public ResponseEntity<List<TeacherClassLessonResponse>> getAllLessonsForTeacherByDateInterval(
      @RequestParam(name = "teacherId") Long teacherId,
      @RequestParam(name = "startDate") LocalDateTime startDate,
      @RequestParam(name = "endDate") LocalDateTime endDate
  )
  {
   return ResponseEntity.ok(teacherClassLessonService.getAllLessonsForTeacherByDateInterval(teacherId,startDate,endDate));
  }

  @PreAuthorize("hasRole('TEACHER')")
  @DeleteMapping("/delete/{teacherClassLessonId}")
  @Operation(summary = "Delete a lesson", description = "Delete a lesson. The user must be a teacher to access this endpoint.")
  public void deleteLesson(@PathVariable Long teacherClassLessonId){
    teacherClassLessonService.deleteLesson(teacherClassLessonId);
  }

  @PreAuthorize("hasRole('TEACHER')")
  @GetMapping("/pending-reservations")
  @Operation(summary = "Get all pending reservations", description = "Get all pending reservations for the current teacher. The user must be a teacher to access this endpoint.")
  public  ResponseEntity<Page<LessonReservationResponse>> getAllPendingReservations(@PageableDefault(size = 15) Pageable pageable)
  {
    Page<LessonReservation> lessons = teacherClassLessonService.getAllPendingLessonsForTeacher(pageable);

    Page<LessonReservationResponse> lessonResponse = lessons.map(LessonReservationResponse::of);
    return ResponseEntity.ok(lessonResponse);
  }

}
