package hu.progressus.controller;

import hu.progressus.dto.CreateTeacherDto;
import hu.progressus.response.AuthResponse;
import hu.progressus.service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/teachers")
public class TeacherController {
  private final TeacherService teacherService;

  @PostMapping("/teacher/registration")
  public ResponseEntity<AuthResponse> registerAsTeacher(@Valid @RequestBody CreateTeacherDto dto){
    return ResponseEntity.ok(teacherService.registerAsTeacher(dto));
  }

  @DeleteMapping("/teacher/delete")
  public void deleteTeacher(){
    teacherService.deleteTeacher();
  }
}
