package hu.progressus.controller;

import hu.progressus.dto.CreateTeacherClassLessonDto;
import hu.progressus.response.TeacherClassLessonResponse;
import hu.progressus.service.TeacherClassLessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/teacher-class-lessons")
@RequiredArgsConstructor
public class TeacherClassLessonController {
  private final TeacherClassLessonService teacherClassLessonService;

  @PostMapping("/create")
  public ResponseEntity<TeacherClassLessonResponse> createLesson(@RequestBody CreateTeacherClassLessonDto dto){
    return ResponseEntity.ok(teacherClassLessonService.createTeacherClassLesson(dto));
  }

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

  @PostMapping("/reservation/{id}/{accepted}")
  public void handleReservation(@PathVariable Long id, @PathVariable boolean accepted){
    teacherClassLessonService.handleReservationStatus(id,accepted);
  }
}
