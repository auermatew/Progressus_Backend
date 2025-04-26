package hu.progressus.servicetests;

import hu.progressus.dto.CreateSubjectsDto;
import hu.progressus.dto.EditSubjectDto;
import hu.progressus.entity.Subject;
import hu.progressus.repository.SubjectRepository;
import hu.progressus.response.SubjectResponse;
import hu.progressus.service.SubjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubjectServiceTest {
  @Mock SubjectRepository subjectRepository;
  @InjectMocks SubjectService subjectService;

  Subject subject1;
  Subject subject2;

  @BeforeEach
  void setup() {
    subject1 = Subject.builder().id(1L).subject("Math").isVerified(true).build();
    subject2 = Subject.builder().id(2L).subject("Science").isVerified(false).build();
  }

  @Test
  void whenSubjectExists_ThrowSubjectExists_throwsConflict() {
    when(subjectRepository.existsBySubject("Math")).thenReturn(true);

    var ex = assertThrows(ResponseStatusException.class, () ->
        subjectService.ThrowSubjectExists("Math"));

    assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
  }

  @Test
  void whenSubjectNotExists_ThrowSubjectExists_doesNotThrow() {
    when(subjectRepository.existsBySubject("Math")).thenReturn(false);

    assertDoesNotThrow(() -> subjectService.ThrowSubjectExists("Math"));
  }

  @Test
  void whenValidInput_createSubject_savesAndReturnsSubject() {
    when(subjectRepository.save(any(Subject.class))).thenReturn(subject1);

    Subject result = subjectService.createSubject("Math", true);

    verify(subjectRepository).save(any(Subject.class));
    assertEquals(subject1, result);
  }

  @Test
  void whenMultipleSubjectsProvided_createMultipleSubjects_savesAllAndReturnsList() {
    List<String> names = List.of("Math", "Science");
    List<Subject> savedList = List.of(subject1, subject2);
    when(subjectRepository.saveAllAndFlush(anyList())).thenReturn(savedList);

    List<Subject> result = subjectService.createMultipleSubjects(names, false);

    verify(subjectRepository).saveAllAndFlush(anyList());
    assertEquals(savedList, result);
  }

  @Test
  void whenExistingAndMissing_findOrCreateSubjectsByName_returnsCombined() {
    List<String> names = List.of("Math", "Biology", "Chemistry");
    when(subjectRepository.findMatchesFromList(anyList())).thenReturn(List.of(subject1));
    when(subjectRepository.saveAllAndFlush(anyList())).thenReturn(List.of(subject2));

    List<Subject> result = subjectService.findOrCreateSubjectsByName(names);

    assertEquals(2, result.size());
    assertTrue(result.contains(subject1));
    assertTrue(result.contains(subject2));
  }

  @Test
  void whenSubjectExists_createVerifiedSubject_throwsConflict() {
    when(subjectRepository.findMatch("Math")).thenReturn(Optional.of(subject1));
    when(subjectRepository.existsBySubject("Math")).thenReturn(true);

    var ex = assertThrows(ResponseStatusException.class, () ->
        subjectService.createVerifiedSubject("Math"));

    assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
  }

  @Test
  void whenSubjectNotExists_createVerifiedSubject_createsVerifiedSubject() {
    when(subjectRepository.findMatch("Math")).thenReturn(Optional.empty());
    when(subjectRepository.save(any(Subject.class))).thenReturn(subject1);

    Subject result = subjectService.createVerifiedSubject("Math");

    verify(subjectRepository).save(any(Subject.class));
    assertEquals(subject1, result);
  }

  @Test
  void whenPageRequest_getAllSubjects_returnsPagedResponses() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<Subject> page = new PageImpl<>(List.of(subject1, subject2));
    when(subjectRepository.findAllByOrderByIdAsc(pageable)).thenReturn(page);

    Page<SubjectResponse> result = subjectService.getAllSubjects(pageable);

    assertEquals(2, result.getContent().size());
    assertEquals("Math", result.getContent().get(0).getSubject());
    assertEquals("Science", result.getContent().get(1).getSubject());
  }

  //region editSubject() tests
  @Test
  void whenSubjectNotFound_editSubject_throwsNotFound() {
    when(subjectRepository.findById(1L)).thenReturn(Optional.empty());

    var ex = assertThrows(ResponseStatusException.class, () ->
        subjectService.editSubject(1L, new EditSubjectDto()));

    assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
  }

  @Test
  void whenSubjectNameConflict_editSubject_throwsConflict() {
    Subject existing = Subject.builder().id(1L).subject("Old").isVerified(true).build();
    when(subjectRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(subjectRepository.existsBySubject("New")).thenReturn(true);

    EditSubjectDto dto = new EditSubjectDto();
    dto.setSubject("New");

    var ex = assertThrows(ResponseStatusException.class, () ->
        subjectService.editSubject(1L, dto));

    assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
  }

  @Test
  void whenValidEdit_editSubject_successfullyEdits() {
    Subject existing = Subject.builder().id(1L).subject("Old").isVerified(true).build();
    when(subjectRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(subjectRepository.existsBySubject("New")).thenReturn(false);
    when(subjectRepository.save(existing)).thenReturn(existing);

    EditSubjectDto dto = new EditSubjectDto();
    dto.setSubject("New");
    dto.setIsVerified(false);

    SubjectResponse response = subjectService.editSubject(1L, dto);

    verify(subjectRepository).save(existing);
    assertEquals("New", response.getSubject());
    assertFalse(response.isVerified());
  }
  //endregion

  //region getSubjectById() tests
  @Test
  void whenSubjectIdNotFound_getSubjectById_throwsNotFound() {
    when(subjectRepository.findById(1L)).thenReturn(Optional.empty());

    var ex = assertThrows(ResponseStatusException.class, () ->
        subjectService.getSubjectById(1L));

    assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
  }

  @Test
  void whenSubjectIdFound_getSubjectById_returnsResponse() {
    when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject1));

    SubjectResponse resp = subjectService.getSubjectById(1L);

    assertEquals("Math", resp.getSubject());
    assertTrue(resp.isVerified());
  }
  //endregion

  //region deleteSubject() tests
  @Test
  void whenSubjectIdNotFound_deleteSubject_throwsNotFound() {
    when(subjectRepository.findById(1L)).thenReturn(Optional.empty());

    var ex = assertThrows(ResponseStatusException.class, () ->
        subjectService.deleteSubject(1L));

    assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
  }

  @Test
  void whenSubjectExists_deleteSubject_deletesSubject() {
    when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject1));

    subjectService.deleteSubject(1L);

    verify(subjectRepository).delete(subject1);
  }
  //endregion
}
