package hu.progressus.controller;

import hu.progressus.dto.CreateTeacherClassLessonDto;
import hu.progressus.response.TeacherClassLessonResponse;
import hu.progressus.service.TeacherClassLessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
  public ResponseEntity<TeacherClassLessonResponse> createLesson(@Valid @RequestBody CreateTeacherClassLessonDto dto){
    return ResponseEntity.ok(teacherClassLessonService.createTeacherClassLesson(dto));
  }

  @PreAuthorize("hasRole('STUDENT')")
  @PostMapping("/reserve/{lessonId}")
  public void reserveLesson(@PathVariable Long lessonId){
    teacherClassLessonService.reserveLesson(lessonId);
  }

  @GetMapping("/teacher/{teacherId}")
  public ResponseEntity<List<TeacherClassLessonResponse>> getAllLessonsForTeacher(@PathVariable Long teacherId){
    return ResponseEntity.ok(teacherClassLessonService.getAllLessonsForTeacher(teacherId));
  }

  @GetMapping("/teacher/{teacherId}/lesson/{lessonId}")
  public ResponseEntity<TeacherClassLessonResponse> getSpecificLessonForTeacher(@PathVariable Long teacherId, @PathVariable Long lessonId){
    return ResponseEntity.ok(teacherClassLessonService.getSpecificLessonForTeacher(teacherId, lessonId));
  }

  @PreAuthorize("hasRole('TEACHER')")
  @PostMapping("/reservation/{id}/{accepted}")
  public void handleReservation(@PathVariable Long id, @PathVariable boolean accepted){
    teacherClassLessonService.handleReservationStatus(id,accepted);
  }

  @GetMapping("/lessons-by/{teacherClassId}")
  public ResponseEntity<List<TeacherClassLessonResponse>> getAllLessonsForTeacherByClasses(@PathVariable Long teacherClassId){
    return ResponseEntity.ok(teacherClassLessonService.getAllLessonsForTeacherByClasses(teacherClassId));
  }

  @GetMapping("/calendar")
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
  public void deleteLesson(@PathVariable Long teacherClassLessonId){
    teacherClassLessonService.deleteLesson(teacherClassLessonId);
  }

}
