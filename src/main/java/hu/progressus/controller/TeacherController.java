package hu.progressus.controller;

import hu.progressus.dto.CreateTeacherDto;
import hu.progressus.dto.EditTeacherDto;
import hu.progressus.entity.Teacher;
import hu.progressus.response.AuthResponse;
import hu.progressus.response.TeacherResponse;
import hu.progressus.service.TeacherService;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/teachers")
public class TeacherController {
  private final TeacherService teacherService;

  @PreAuthorize("hasRole('STUDENT')")
  @PostMapping("/teacher/registration")
  @Operation(summary = "Register as a teacher", description = "Register as a teacher. The user must be a student to access this endpoint.")
  public ResponseEntity<AuthResponse> registerAsTeacher(@Valid @RequestBody CreateTeacherDto dto){
    return ResponseEntity.ok(teacherService.registerAsTeacher(dto));
  }

  @PreAuthorize("hasRole('TEACHER')")
  @DeleteMapping("/teacher/delete")
  @Operation(summary = "Delete teacher", description = "Changes the TEACHER role to STUDENT. The user must be a teacher to access this endpoint.")
  public void deleteTeacher(){
    teacherService.deleteTeacher();
  }

  @GetMapping("/teacher/{teacherId}")
  @Operation(summary = "Get teacher by ID", description = "Get a teacher by ID.")
  public ResponseEntity<TeacherResponse> getTeacherById(@PathVariable Long teacherId){
    return ResponseEntity.ok(TeacherResponse.of(teacherService.getTeacherById(teacherId)));
  }

  @GetMapping("/all")
  @Operation(summary = "Get all teachers", description = "Get all teachers.")
  public ResponseEntity<Page<TeacherResponse>> getAllTeachers(@PageableDefault(size = 15)
      Pageable pageable){
    Page<Teacher> teacherPage = teacherService.getAllTeachers(pageable);

    Page<TeacherResponse> teacherResponses = teacherPage.map(TeacherResponse::of);
    return ResponseEntity.ok(teacherResponses);
  }

  @PreAuthorize("hasRole('TEACHER')")
  @PatchMapping("/teacher/edit")
  @Operation(summary = "Edit teacher", description = "Edit teacher information. The user must be a teacher to access this endpoint.")
  public ResponseEntity<TeacherResponse> editTeacher(@RequestBody @Valid EditTeacherDto dto) {
    return ResponseEntity.ok(teacherService.editTeacher(dto));
  }
}
