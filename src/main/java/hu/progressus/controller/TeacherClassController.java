package hu.progressus.controller;

import hu.progressus.dto.CreateTeacherClassDto;
import hu.progressus.dto.EditTeacherClassDto;
import hu.progressus.response.TeacherClassResponse;
import hu.progressus.service.TeacherClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

  @PostMapping("/create")
  public ResponseEntity<TeacherClassResponse> createTeacherClass(@RequestBody CreateTeacherClassDto dto){
    return ResponseEntity.ok(teacherClassService.createTeacherClass(dto));
  }

  @PatchMapping("/edit/{teacherClassId}")
  public ResponseEntity<TeacherClassResponse> editTeacherClass(@PathVariable Long teacherClassId, @RequestBody EditTeacherClassDto dto){
    return ResponseEntity.ok(teacherClassService.editTeacherClass(teacherClassId, dto));
  }


  @DeleteMapping("/delete/{teacherClassId}")
  public void deleteTeacherClass(@PathVariable Long teacherClassId){
    teacherClassService.deleteTeacherClass(teacherClassId);
  }

  @GetMapping("/teacher/{teacherId}")
  public ResponseEntity<List<TeacherClassResponse>> getClassesOfTeacher(@PathVariable Long teacherId){
    return ResponseEntity.ok(teacherClassService.getClassesOfTeacher(teacherId));
  }

}
