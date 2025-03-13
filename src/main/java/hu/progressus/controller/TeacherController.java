package hu.progressus.controller;

import hu.progressus.dto.CreateTeacherDto;
import hu.progressus.dto.EditTeacherDto;
import hu.progressus.entity.Teacher;
import hu.progressus.response.AuthResponse;
import hu.progressus.response.TeacherResponse;
import hu.progressus.service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
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

  @PostMapping("/teacher/registration")
  public ResponseEntity<AuthResponse> registerAsTeacher(@Valid @RequestBody CreateTeacherDto dto){
    return ResponseEntity.ok(teacherService.registerAsTeacher(dto));
  }

  @DeleteMapping("/teacher/delete")
  public void deleteTeacher(){
    teacherService.deleteTeacher();
  }

  @GetMapping("/teacher/{teacherId}")
  public ResponseEntity<TeacherResponse> getTeacherById(@PathVariable Long teacherId){
    return ResponseEntity.ok(TeacherResponse.of(teacherService.getTeacherById(teacherId)));
  }

  @GetMapping("/all")
  public ResponseEntity<Page<TeacherResponse>> getAllTeachers(@PageableDefault(size = 15)
      Pageable pageable){
    Page<Teacher> teacherPage = teacherService.getAllTeachers(pageable);

    Page<TeacherResponse> teacherResponses = teacherPage.map(TeacherResponse::of);
    return ResponseEntity.ok(teacherResponses);
  }

  @PatchMapping("/teacher/edit")
  public ResponseEntity<TeacherResponse> editTeacher(@RequestBody @Valid EditTeacherDto dto) {
    return ResponseEntity.ok(teacherService.editTeacher(dto));
  }
}
