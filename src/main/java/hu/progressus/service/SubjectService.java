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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * CRUD operations for Subject entities, including multiple subject creation.
 * Update,insert logic based on formatted subject names.
 */
@Service
@RequiredArgsConstructor
public class SubjectService {
  private final SubjectRepository subjectRepository;

  /**
   * Throws if a subject with the given name already exists.
   *
   * @param subject the formatted subject name
   * @throws ResponseStatusException on conflict
   */
  public void ThrowSubjectExists(String subject){
    if (subjectRepository.existsBySubject(subject)){
      throw new ResponseStatusException(HttpStatus.CONFLICT,"subject already exists");
    }
  }

  /**
   * Create and persist a new Subject.
   *
   * @param subjectName raw name to format & save
   * @param isVerified whether to mark as verified
   * @return the saved Subject
   */
  public Subject createSubject(String subjectName, boolean isVerified) {
    String formattedName = StringFormatter.formatString(subjectName);
    var newSubject = Subject.builder()
        .subject(formattedName)
        .isVerified(isVerified)
        .build();
    return subjectRepository.save(newSubject);
  }

  /**
   * Create multiple subjects in bulk.
   *
   * @param subjectNames list of names
   * @param isVerified verification flag
   * @return list of created Subjects
   */
  public List<Subject> createMultipleSubjects(List<String> subjectNames, boolean isVerified) {
    List<Subject> newSubjects = subjectNames.stream().map(name ->
            Subject.builder()
                .subject(name)
                .isVerified(isVerified)
                .build())
        .toList();
    return subjectRepository.saveAllAndFlush(newSubjects);
  }

  /**
   * Find existing subjects by name, and create any missing ones (unverified).
   *
   * @param subjectNames raw list of names
   * @return combined list of existing + newly created subjects
   */
  public List<Subject> findOrCreateSubjectsByName(List<String> subjectNames) {
    List<String> formattedNames = subjectNames.stream()
        .map(StringFormatter::formatString)
        .distinct()
        .toList();

    List<Subject> existingSubjects = subjectRepository.findMatchesFromList(formattedNames);
    System.out.println("existingS:" + existingSubjects);
    Set<String> existingNames = existingSubjects.stream()
        .map(Subject::getSubject)
        .collect(Collectors.toSet());
    System.out.println("existingNames:" + existingNames);
    List<String> missingNames = formattedNames.stream()
        .filter(name -> !existingNames.contains(name))
        .toList();
    System.out.println("missingNames:" + missingNames);

    List<Subject> newSubjects = createMultipleSubjects(missingNames, false);

    List<Subject> allSubjects = new ArrayList<>();
    allSubjects.addAll(existingSubjects);
    allSubjects.addAll(newSubjects);

    return allSubjects;
  }

  /**
   * Alias for findOrCreateSubjectsByName.
   */
  public List<Subject> findOrCreateSubjects(List<String> subjects){
    return findOrCreateSubjectsByName(subjects);
  }

  /**
   * Create a single verified subject, erroring if already exists.
   *
   * @param subjectName raw name
   * @return the saved, verified Subject
   * @throws ResponseStatusException on conflict
   */
  public Subject createVerifiedSubject(String subjectName) {
    String formattedName = StringFormatter.formatString(subjectName);
    Optional<Subject> existingSubject = subjectRepository.findMatch(formattedName);
    if (existingSubject.isPresent()) {
      ThrowSubjectExists(formattedName);
    }
    return createSubject(subjectName, true);
  }

  /**
   * Admin-only: create multiple verified subjects.
   *
   * @param dto contains list of subject names
   * @return list of SubjectResponse DTOs
   */
  public List<SubjectResponse> createSubjectsForAdmin(CreateSubjectsDto dto) {
    return dto.getSubjectNames().stream()
        .map(this::createVerifiedSubject)
        .map(SubjectResponse::of)
        .toList();
  }

  /**
   * Page through all subjects, ordered by ID.
   *
   * @param pageable pagination rules
   * @return page of SubjectResponse DTOs
   */
  public Page<SubjectResponse> getAllSubjects(Pageable pageable) {
    return subjectRepository.findAllByOrderByIdAsc(pageable)
        .map(SubjectResponse::of);
  }

  /**
   * Admin-only: Update a subject’s name and/or verification flag.
   *
   * @param subjectId the ID to edit
   * @param dto the changes to apply
   * @return the updated SubjectResponse DTO
   * @throws ResponseStatusException if not found or name conflict
   */
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

  /**
   * Retrieve a single subject by ID.
   *
   * @param subjectId the target ID
   * @return the SubjectResponse DTO
   * @throws ResponseStatusException if missing
   */
  public SubjectResponse getSubjectById(Long subjectId) {
    Subject subject = subjectRepository.findById(subjectId)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Subject not found"));
    return SubjectResponse.of(subject);
  }


  /**
   * Admin-only: Delete a subject by ID.
   *
   * @param subjectId the ID to remove
   * @throws ResponseStatusException if missing
   */
  @Transactional
  public void deleteSubject(Long subjectId) {
    Subject subject = subjectRepository.findById(subjectId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subject not found"));
    subjectRepository.delete(subject);
  }


}
