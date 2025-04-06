package hu.progressus.controller;

import hu.progressus.dto.CreateSubjectsDto;
import hu.progressus.dto.EditSubjectDto;
import hu.progressus.response.SubjectResponse;
import hu.progressus.service.SubjectService;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/subjects")
@RequiredArgsConstructor
public class SubjectController {

  private final SubjectService subjectService;

  @GetMapping("/all")
  public Page<SubjectResponse> getAllSubjects(@PageableDefault(size = 15) Pageable pageable) {
    return subjectService.getAllSubjects(pageable);
  }

  @GetMapping("/{subjectId}")
  public ResponseEntity<SubjectResponse> getSubjectById(@PathVariable Long subjectId) {
    SubjectResponse response = subjectService.getSubjectById(subjectId);
    return ResponseEntity.ok(response);
  }


  @PatchMapping("/edit/{subjectId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SubjectResponse> editSubject(@PathVariable Long subjectId, @RequestBody EditSubjectDto dto) {
    SubjectResponse response = subjectService.editSubject(subjectId, dto);
    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/create")
  public ResponseEntity<List<SubjectResponse>> createSubjects(@Valid @RequestBody CreateSubjectsDto dto) {
    List<SubjectResponse> response = subjectService.createSubjectsForAdmin(dto);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/delete/{subjectId}")
  @PreAuthorize("hasRole('ADMIN')")
  public void deleteSubject(@PathVariable Long subjectId) {
    subjectService.deleteSubject(subjectId);
  }

}
