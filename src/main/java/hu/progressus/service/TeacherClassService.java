package hu.progressus.service;

import hu.progressus.dto.CreateTeacherClassDto;
import hu.progressus.entity.TeacherClass;
import hu.progressus.entity.User;
import hu.progressus.repository.TeacherClassRepository;
import hu.progressus.response.TeacherClassResponse;
import hu.progressus.util.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeacherClassService {
  private final TeacherClassRepository teacherClassRepository;
  private final UserUtils userUtils;

  public TeacherClassResponse createTeacherClass(CreateTeacherClassDto dto){
    User user = userUtils.currentUser();
    TeacherClass teacherClass = TeacherClass.builder()
        .teacher(user.getTeacher())
        .title(dto.getTitle())
        .description(dto.getDescription())
        .price(dto.getPrice())
        .build();
    teacherClassRepository.save(teacherClass);
    return TeacherClassResponse.of(teacherClass);
  }

  public void deleteTeacherClass(Long teacherClassId){
    User user = userUtils.currentUser();
    TeacherClass teacherClass = teacherClassRepository.findTeacherClassByIdAndTeacher_User_Id(teacherClassId,user.getId()).orElseThrow();
    teacherClassRepository.delete(teacherClass);
  }

  public List<TeacherClassResponse> getClassesOfTeacher(Long teacherId){
    return teacherClassRepository.findAllByTeacher_Id(teacherId).stream().map(TeacherClassResponse::of).toList();
  }
}
