package hu.progressus.controller;

import hu.progressus.dto.CreateTeacherClassDto;
import hu.progressus.dto.EditTeacherClassDto;
import hu.progressus.response.TeacherClassResponse;
import hu.progressus.service.TeacherClassService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/teacher-classes")
public class TeacherClassController {
  private final TeacherClassService teacherClassService;

  @PreAuthorize("hasRole('TEACHER')")
  @PostMapping("/create")
  @Operation(summary = "Create a teacher class", description = "Create a teacher class. The user must be a teacher to access this endpoint.")
  public ResponseEntity<TeacherClassResponse> createTeacherClass(@Valid @RequestBody CreateTeacherClassDto dto){
    return ResponseEntity.ok(teacherClassService.createTeacherClass(dto));
  }

  @PreAuthorize("hasRole('TEACHER')")
  @PatchMapping("/edit/{teacherClassId}")
  @Operation(summary = "Edit a teacher class", description = "Edit a teacher class. The user must be a teacher to access this endpoint.")
  public ResponseEntity<TeacherClassResponse> editTeacherClass(@PathVariable Long teacherClassId, @RequestBody EditTeacherClassDto dto){
    return ResponseEntity.ok(teacherClassService.editTeacherClass(teacherClassId, dto));
  }


  @PreAuthorize("hasRole('TEACHER')")
  @DeleteMapping("/delete/{teacherClassId}")
  @Operation(summary = "Delete a teacher class", description = "Delete a teacher class. The user must be a teacher to access this endpoint.")
  public void deleteTeacherClass(@PathVariable Long teacherClassId){
    teacherClassService.deleteTeacherClass(teacherClassId);
  }

  @GetMapping("/teacher/{teacherId}")
  @Operation(summary = "Get all classes of a teacher", description = "Get all classes of a teacher.")
  public ResponseEntity<List<TeacherClassResponse>> getTeacherClassesOfTeacher(@PathVariable Long teacherId){
    return ResponseEntity.ok(teacherClassService.getTeacherClassesOfTeacher(teacherId));
  }

  @GetMapping("/teacher/specific-class/{teacherClassId}")
  @Operation(summary = "Get a specific class of a teacher", description = "Get a specific class of a teacher by teacherclass ID.")
  public ResponseEntity<TeacherClassResponse> getTeacherClassById(@PathVariable Long teacherClassId){
    return ResponseEntity.ok(teacherClassService.getTeacherClassById(teacherClassId));
  }

}
