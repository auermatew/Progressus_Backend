package hu.progressus.service;

import hu.progressus.dto.CreateTeacherDto;
import hu.progressus.dto.EditTeacherDto;
import hu.progressus.entity.Teacher;
import hu.progressus.entity.User;
import hu.progressus.enums.Role;
import hu.progressus.repository.TeacherRepository;
import hu.progressus.repository.UserRepository;
import hu.progressus.response.AuthResponse;
import hu.progressus.response.TeacherResponse;
import hu.progressus.util.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
public class TeacherService {
  private final UserUtils userUtils;
  private final UserRepository userRepository;
  private final UserService userService;
  private final TeacherRepository teacherRepository;

  public AuthResponse registerAsTeacher(CreateTeacherDto dto){
    User user = userUtils.currentUser();
    if(user.getTeacher() != null){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "already a teacher");
    }
    userService.ThrowUserEmailExists(dto.getContactEmail());
    userService.ThrowUserPhoneExists(dto.getContactPhone());
    user.setTeacher(
        Teacher.builder().user(user)
        .contactEmail(dto.getContactEmail())
        .contactPhone(dto.getContactPhone())
        .build()
    );
    user.setRole(Role.ROLE_TEACHER);
    userRepository.save(user);
    return AuthResponse.of(user);
  }

  public void deleteTeacher(){
    User user = userUtils.currentUser();
    if(user.getTeacher() == null){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "not a teacher");
    }

    user.setTeacher(null);
    user.setRole(Role.ROLE_STUDENT);
    userRepository.save(user);
  }

  public Teacher getTeacherById(Long teacherId){
    return teacherRepository.findById(teacherId).orElseThrow();
  }

  public Page<Teacher> getAllTeachers(Pageable pageable){
    return teacherRepository.findAll(pageable);
  }

  public TeacherResponse editTeacher(EditTeacherDto dto){
    User user = userUtils.currentUser();
    if(user.getTeacher() == null){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "not a teacher");
    }
    Teacher teacher = user.getTeacher();
    if(dto.getContactEmail() != null){
      teacher.setContactEmail(dto.getContactEmail());
    }
    if(dto.getContactPhone() != null){
      teacher.setContactPhone(dto.getContactPhone());
    }
    teacher = teacherRepository.save(teacher);
    return TeacherResponse.of(teacher);
  }
}
