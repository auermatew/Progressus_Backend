package hu.progressus.service;

import hu.progressus.dto.CreateSubjectsDto;
import hu.progressus.dto.EditSubjectDto;
import hu.progressus.entity.Subject;
import hu.progressus.repository.SubjectRepository;
import hu.progressus.response.SubjectResponse;
import hu.progressus.util.StringFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubjectService {
  private final SubjectRepository subjectRepository;

  public void ThrowSubjectExists(String subject){
    if (subjectRepository.existsBySubject(subject)){
      throw new ResponseStatusException(HttpStatus.CONFLICT,"subject already exists");
    }
  }

  private Subject createSubject(String subjectName, boolean isVerified) {
    String formattedName = StringFormatter.formatString(subjectName);
    var newSubject = Subject.builder()
        .subject(formattedName)
        .isVerified(isVerified)
        .build();
    return subjectRepository.save(newSubject);
  }

  public Subject findOrCreateSubjectByName(String subjectName){
    String formattedName = StringFormatter.formatString(subjectName);
    Optional<Subject> existingSubject = subjectRepository.findMatch(formattedName);
    return existingSubject.orElseGet(() -> createSubject(subjectName, false));
  }
  public List<Subject> findOrCreateSubjects(List<String> subjects){
    return subjects.stream()
        .map(this::findOrCreateSubjectByName)
        .toList();
  }

  public Subject createVerifiedSubject(String subjectName) {
    String formattedName = StringFormatter.formatString(subjectName);
    Optional<Subject> existingSubject = subjectRepository.findMatch(formattedName);
    if (existingSubject.isPresent()) {
      ThrowSubjectExists(formattedName);
    }
    return createSubject(subjectName, true);
  }
  public List<SubjectResponse> createSubjectsForAdmin(CreateSubjectsDto dto) {
    return dto.getSubjectNames().stream()
        .map(this::createVerifiedSubject)
        .map(SubjectResponse::of)
        .toList();
  }

  public Page<SubjectResponse> getAllSubjects(Pageable pageable) {
    return subjectRepository.findAllByOrderByIdAsc(pageable)
        .map(SubjectResponse::of);
  }

  //@PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public SubjectResponse editSubject(Long subjectId, EditSubjectDto dto) {
    Subject subject = subjectRepository.findById(subjectId)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Subject not found"));
    if (dto.getSubject() != null) {
      String formattedName = StringFormatter.formatString(dto.getSubject());
      if (!subject.getSubject().equals(formattedName)) {
        ThrowSubjectExists(formattedName);
        subject.setSubject(formattedName);
      }
    }
    if (dto.getIsVerified() != null) {
      subject.setVerified(dto.getIsVerified());
    }
    subjectRepository.save(subject);
    return SubjectResponse.of(subject);
  }

  public SubjectResponse getSubjectById(Long subjectId) {
    Subject subject = subjectRepository.findById(subjectId)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Subject not found"));
    return SubjectResponse.of(subject);
  }


  //@PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public void deleteSubject(Long subjectId) {
    Subject subject = subjectRepository.findById(subjectId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subject not found"));
    subjectRepository.delete(subject);
  }


}
