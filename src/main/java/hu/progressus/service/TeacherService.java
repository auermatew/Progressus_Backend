package hu.progressus.service;

import hu.progressus.dto.CreateTeacherDto;
import hu.progressus.entity.Teacher;
import hu.progressus.entity.User;
import hu.progressus.enums.Role;
import hu.progressus.repository.TeacherRepository;
import hu.progressus.repository.UserRepository;
import hu.progressus.response.AuthResponse;
import hu.progressus.util.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
public class TeacherService {
  private final UserUtils userUtils;
  private final UserRepository userRepository;
  private final TeacherRepository teacherRepository;

  public AuthResponse registerAsTeacher(CreateTeacherDto dto){
    User user = userUtils.currentUser();
    if(user.getTeacher() != null){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "already a teacher");
    }
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

  public Teacher getTeacher(){
    return null;
  }
}
