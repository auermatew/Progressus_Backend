package hu.progressus.service;

import hu.progressus.dto.CreateTeacherClassDto;
import hu.progressus.dto.EditTeacherClassDto;
import hu.progressus.entity.Subject;
import hu.progressus.entity.TeacherClass;
import hu.progressus.entity.TeacherClassSubject;
import hu.progressus.entity.User;
import hu.progressus.repository.SubjectRepository;
import hu.progressus.repository.TeacherClassRepository;
import hu.progressus.repository.TeacherClassSubjectRepository;
import hu.progressus.response.TeacherClassResponse;
import hu.progressus.util.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/**
 * CRUD operations on TeacherClass entities, including linking to Subjects.
 */
@Service
@RequiredArgsConstructor
public class TeacherClassService {
  private final TeacherClassRepository teacherClassRepository;
  private final TeacherClassSubjectRepository teacherClassSubjectRepository;
  private final UserUtils userUtils;
  private final SubjectService subjectService;
  private final SubjectRepository subjectRepository;

  /**
   * Create a new TeacherClass and attach subjects.
   *
   * @param dto the DTO containing class details and subject names
   * @return a DTO of the created TeacherClass
   */
  @Transactional
  public TeacherClassResponse createTeacherClass(CreateTeacherClassDto dto){
    User user = userUtils.currentUser();
    TeacherClass teacherClass = TeacherClass.builder()
        .teacher(user.getTeacher())
        .title(dto.getTitle())
        .description(dto.getDescription())
        .price(dto.getPrice())
        .build();
    teacherClassRepository.save(teacherClass);

    List<String> subjectNames = dto.getSubjects();
    if (subjectNames == null || subjectNames.isEmpty()) {
      return TeacherClassResponse.of(teacherClass);
    }

    List<Subject> subjectList = subjectService.findOrCreateSubjects(subjectNames);
    for(Subject subject :subjectList){
      TeacherClassSubject tcs = TeacherClassSubject.builder()
          .teacherClass(teacherClass)
          .subject(subject)
          .build();
      teacherClassSubjectRepository.save(tcs);
      teacherClass.getSubjects().add(tcs);
    }
    return TeacherClassResponse.of(teacherClass);
  }

  /**
   * Edit an existing TeacherClass owned by the current teacher.
   * Supports updating basic fields and managing subject associations.
   *
   * @param teacherClassId the ID to edit
   * @param dto changes to apply
   * @return the updated DTO
   * @throws ResponseStatusException if not found or unauthorized
   */
  @Transactional
  public TeacherClassResponse editTeacherClass(Long teacherClassId, EditTeacherClassDto dto) {
    User user = userUtils.currentUser();

    TeacherClass teacherClass = teacherClassRepository
        .findTeacherClassByIdAndTeacher_User_Id(teacherClassId, user.getId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Class not found"));

    modifyByDto(teacherClass, dto);

    teacherClass = teacherClassRepository.save(teacherClass);
    return TeacherClassResponse.of(teacherClass);
  }

  /**
   * Update fields of a TeacherClass based on the provided DTO.
   * Only non-null fields in the DTO will be updated.
   *
   * @param teacherClass the target TeacherClass
   * @param dto the changes to apply
   */
  private void modifyByDto(TeacherClass teacherClass, EditTeacherClassDto dto) {
    if (dto.getTitle() != null) {
      teacherClass.setTitle(dto.getTitle());
    }
    if (dto.getDescription() != null) {
      teacherClass.setDescription(dto.getDescription());
    }
    if (dto.getPrice() != null) {
      teacherClass.setPrice(dto.getPrice());
    }
    if (dto.getSubjectIdsToRemove() != null && !dto.getSubjectIdsToRemove().isEmpty()) {
      deleteUnusedSubjects(teacherClass,dto);
    }
    if (dto.getSubjectsToAdd() != null && !dto.getSubjectsToAdd().isEmpty()) {
      addNotExistingSubjects(teacherClass,dto);
    }
  }

  /**
   * Add new subjects to a TeacherClass, creating them if they don't exist.
   * Also checks for existing links to avoid duplicates.
   *
   * @param teacherClass the target TeacherClass
   * @param dto the DTO containing new subject names
   */
  private void addNotExistingSubjects(TeacherClass teacherClass, EditTeacherClassDto dto) {
    List<Subject> newSubjects = subjectService.findOrCreateSubjects(dto.getSubjectsToAdd());
    List<TeacherClassSubject> newTeacherClassSubjects = new ArrayList<>();
    for (Subject subject : newSubjects) {
      boolean alreadyLinked = teacherClass.getSubjects().stream()
          .anyMatch(tcs -> tcs.getSubject().getId().equals(subject.getId()));
      if (!alreadyLinked) {
        TeacherClassSubject tcs = TeacherClassSubject.builder()
            .teacherClass(teacherClass)
            .subject(subject)
            .build();
        newTeacherClassSubjects.add(tcs);
      }
      teacherClassSubjectRepository.saveAllAndFlush(newTeacherClassSubjects);
      teacherClass.getSubjects().addAll(newTeacherClassSubjects);
    }

  }

  /**
   * Remove subjects from a TeacherClass and delete them if they are not linked to any other classes.
   *
   * @param teacherClass the target TeacherClass
   * @param dto the DTO containing subject IDs to remove
   */
  private void deleteUnusedSubjects(TeacherClass teacherClass, EditTeacherClassDto dto) {
    List<TeacherClassSubject> toRemove = teacherClassSubjectRepository
        .findAllByTeacherClassIdAndSubjectIdIn(teacherClass.getId(), dto.getSubjectIdsToRemove());
    teacherClassSubjectRepository.deleteAll(toRemove);
    teacherClassSubjectRepository.flush();
    teacherClass.getSubjects().removeAll(toRemove);

    List<Long> subjectIdsToCheck = toRemove.stream()
        .map(tcs -> tcs.getSubject().getId())
        .distinct()
        .toList();

    List<Long> deletableSubjectIds = subjectRepository.findUnusedUnverifiedSubjectIds(subjectIdsToCheck);

    if (!deletableSubjectIds.isEmpty()) {
      subjectRepository.deleteByIdIn(deletableSubjectIds);
    }

  }

  /**
   * Delete a TeacherClass owned by the current teacher.
   *
   * @param teacherClassId the ID of the class to delete
   * @throws ResponseStatusException if not found or unauthorized
   */
  public void deleteTeacherClass(Long teacherClassId){
    User user = userUtils.currentUser();
    TeacherClass teacherClass = teacherClassRepository.findTeacherClassByIdAndTeacher_User_Id(teacherClassId,user.getId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Class not found"));
    teacherClassRepository.delete(teacherClass);
  }

  /**
   * Fetch all TeacherClasses owned by the current teacher.
   *
   * @return list of TeacherClassResponse DTOs
   */
  public List<TeacherClassResponse> getTeacherClassesOfTeacher(Long teacherId){
    return teacherClassRepository.findAllByTeacher_Id(teacherId).stream().map(TeacherClassResponse::of).toList();
  }

  /**
   * Fetch a specific TeacherClass by ID.
   *
   * @param teacherClassId the target ID
   * @return the TeacherClassResponse DTO
   * @throws ResponseStatusException if not found
   */
  public TeacherClassResponse getTeacherClassById(Long teacherClassId){

    TeacherClass teacherClass = teacherClassRepository.findById(teacherClassId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Class not found"));
    return TeacherClassResponse.of(teacherClass);
  }
}
