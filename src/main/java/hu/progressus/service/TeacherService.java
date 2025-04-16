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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Manages the Teacher profiles:
 * registration, deletion, editing, and listing.
 */
@RequiredArgsConstructor
@Service
public class TeacherService {
  private final UserUtils userUtils;
  private final UserRepository userRepository;
  private final UserService userService;
  private final TeacherRepository teacherRepository;

  /**
   * Registers the currently authenticated user as a teacher.
   *
   * @param dto the DTO containing contact fields
   * @return an AuthResponse DTO of the saved entity
   * @throws ResponseStatusException if the user is already a teacher
   */
  @CacheEvict(
      value = "userCache",
      key = "@userUtils.currentUser().id"
  )
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

  /**
   * Deletes the currently authenticated user's teacher profile.
   *
   * @throws ResponseStatusException if the user is not a teacher
   */
  @CacheEvict(
      value = "userCache",
      key = "@userUtils.currentUser().id"
  )
  public void deleteTeacher(){
    User user = userUtils.currentUser();
    if(user.getTeacher() == null){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "not a teacher");
    }

    user.setTeacher(null);
    user.setRole(Role.ROLE_STUDENT);
    userRepository.save(user);
  }

  /**
   * Retrieves the currently authenticated user's teacher profile.
   *
   * @return a TeacherResponse DTO of the saved entity
   * @throws ResponseStatusException if the user is not a teacher
   */
  public Teacher getTeacherById(Long teacherId){
    return teacherRepository.findById(teacherId).orElseThrow();
  }

  /**
   * Retrieves all teachers with pagination.
   *
   * @param pageable the pagination information
   * @return a Page of TeacherResponse DTOs
   */
  public Page<Teacher> getAllTeachers(Pageable pageable){
    return teacherRepository.findAllByOrderByIdAsc(pageable);
  }

  /**
   * Edits the currently authenticated user's teacher profile.
   *
   * @param dto the DTO containing contact fields
   * @return a TeacherResponse DTO of the saved entity
   * @throws ResponseStatusException if the user is not a teacher
   */
  @CacheEvict(
      value = "userCache",
      key = "@userUtils.currentUser().id"
  )
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
