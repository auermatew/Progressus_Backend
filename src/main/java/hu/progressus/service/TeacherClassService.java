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

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeacherClassService {
  private final TeacherClassRepository teacherClassRepository;
  private final TeacherClassSubjectRepository teacherClassSubjectRepository;
  private final UserUtils userUtils;
  private final SubjectService subjectService;
  private final SubjectRepository subjectRepository;

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

  //TODO: Cleanup this mess, i made this code at 3am, but it works haha
  @Transactional
  public TeacherClassResponse editTeacherClass(Long teacherClassId, EditTeacherClassDto dto) {
    User user = userUtils.currentUser();
    TeacherClass teacherClass = teacherClassRepository
        .findTeacherClassByIdAndTeacher_User_Id(teacherClassId, user.getId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Class not found"));
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
      List<TeacherClassSubject> toRemove = teacherClassSubjectRepository
          .findAllByTeacherClassIdAndSubjectIdIn(teacherClass.getId(), dto.getSubjectIdsToRemove());
      teacherClassSubjectRepository.deleteAll(toRemove);
      teacherClass.getSubjects().removeAll(toRemove);
      toRemove.stream()
          .map(tcs -> tcs.getSubject().getId())
          .distinct()
          .forEach(subjectId -> {
            Subject subject = subjectRepository.findById(subjectId).orElse(null);

            if (subject != null && !subject.isVerified()) {
              boolean stillUsed = teacherClassSubjectRepository.existsBySubjectId(subjectId);
              if (!stillUsed) {
                subjectRepository.delete(subject);
              }
            }
          });
    }

    if (dto.getSubjectsToAdd() != null && !dto.getSubjectsToAdd().isEmpty()) {
      List<Subject> newSubjects = subjectService.findOrCreateSubjects(dto.getSubjectsToAdd());
      for (Subject subject : newSubjects) {
        boolean alreadyLinked = teacherClass.getSubjects().stream()
            .anyMatch(tcs -> tcs.getSubject().getId().equals(subject.getId()));
        if (!alreadyLinked) {
          TeacherClassSubject tcs = TeacherClassSubject.builder()
              .teacherClass(teacherClass)
              .subject(subject)
              .build();
          teacherClassSubjectRepository.save(tcs);
          teacherClass.getSubjects().add(tcs);
        }
      }
    }
    teacherClassRepository.save(teacherClass);
    return TeacherClassResponse.of(teacherClass);
  }


  public void deleteTeacherClass(Long teacherClassId){
    User user = userUtils.currentUser();
    TeacherClass teacherClass = teacherClassRepository.findTeacherClassByIdAndTeacher_User_Id(teacherClassId,user.getId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Class not found"));
    teacherClassRepository.delete(teacherClass);
  }

  public List<TeacherClassResponse> getTeacherClassesOfTeacher(Long teacherId){
    return teacherClassRepository.findAllByTeacher_Id(teacherId).stream().map(TeacherClassResponse::of).toList();
  }

  public TeacherClassResponse getTeacherClassById(Long teacherClassId){

    TeacherClass teacherClass = teacherClassRepository.findById(teacherClassId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Class not found"));
    return TeacherClassResponse.of(teacherClass);
  }
}
