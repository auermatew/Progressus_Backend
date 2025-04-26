package hu.progressus.servicetests;

import hu.progressus.dto.CreateTeacherDto;
import hu.progressus.dto.EditTeacherDto;
import hu.progressus.entity.Teacher;
import hu.progressus.entity.User;
import hu.progressus.enums.Role;
import hu.progressus.repository.TeacherRepository;
import hu.progressus.repository.UserRepository;
import hu.progressus.response.AuthResponse;
import hu.progressus.response.TeacherResponse;
import hu.progressus.service.TeacherService;
import hu.progressus.service.UserService;
import hu.progressus.util.UserUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeacherServiceTest {
  @Mock UserUtils userUtils;
  @Mock UserRepository userRepository;
  @Mock UserService userService;
  @Mock TeacherRepository teacherRepository;

  @InjectMocks TeacherService teacherService;

  User user;
  CreateTeacherDto createDto;
  EditTeacherDto editDto;
  Teacher storedTeacher;

  @BeforeEach
  void setup() {
    user = User.builder()
        .id(1L)
        .role(Role.ROLE_STUDENT)
        .build();
    // initially not a teacher
    user.setTeacher(null);

    createDto = new CreateTeacherDto();
    createDto.setContactEmail("t@example.com");
    createDto.setContactPhone("123456");

    editDto = new EditTeacherDto();
    editDto.setContactEmail("new@example.com");
    editDto.setContactPhone("654321");

    // storedTeacher for repository find
    storedTeacher = Teacher.builder()
        .id(10L)
        .user(user)
        .contactEmail("old@example.com")
        .contactPhone("000000")
        .build();
  }

  //region registerAsTeacher() tests
  @Test
  void whenUserAlreadyTeacher_registerAsTeacher_ThrowsBadRequest() {
    user.setTeacher(storedTeacher);
    when(userUtils.currentUser()).thenReturn(user);

    var ex = assertThrows(ResponseStatusException.class, () ->
        teacherService.registerAsTeacher(createDto));

    assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    assertEquals("already a teacher", ex.getReason());
  }

  @Test
  void whenContactEmailInUse_registerAsTeacher_ThrowsConflict() {
    when(userUtils.currentUser()).thenReturn(user);
    when(teacherRepository.existsByContactEmail(createDto.getContactEmail())).thenReturn(true);

    var ex = assertThrows(ResponseStatusException.class, () ->
        teacherService.registerAsTeacher(createDto));

    assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    assertEquals("Contact email already in use", ex.getReason());
  }

  @Test
  void whenContactPhoneInUse_registerAsTeacher_ThrowsConflict() {
    when(userUtils.currentUser()).thenReturn(user);
    when(teacherRepository.existsByContactEmail(createDto.getContactEmail())).thenReturn(false);
    when(teacherRepository.existsByContactPhone(createDto.getContactPhone())).thenReturn(true);

    var ex = assertThrows(ResponseStatusException.class, () ->
        teacherService.registerAsTeacher(createDto));

    assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    assertEquals("Contact phone already in use", ex.getReason());
  }

  @Test
  void whenValidDto_registerAsTeacher_SetsTeacherAndRole() {
    when(userUtils.currentUser()).thenReturn(user);
    when(teacherRepository.existsByContactEmail(createDto.getContactEmail())).thenReturn(false);
    when(teacherRepository.existsByContactPhone(createDto.getContactPhone())).thenReturn(false);
    when(userRepository.save(user)).thenReturn(user);

    AuthResponse resp = teacherService.registerAsTeacher(createDto);

    verify(userRepository).save(user);
    assertNotNull(user.getTeacher());
    assertEquals(createDto.getContactEmail(), user.getTeacher().getContactEmail());
    assertEquals(createDto.getContactPhone(), user.getTeacher().getContactPhone());
    assertEquals(Role.ROLE_TEACHER, user.getRole());
    assertNotNull(resp);
    assertEquals(Role.ROLE_TEACHER, resp.getRole());
  }
  //endregion

  //region deleteTeacher() tests
  @Test
  void whenUserNotATeacher_deleteTeacher_ThrowsBadRequest() {
    // user.teacher == null
    when(userUtils.currentUser()).thenReturn(user);

    var ex = assertThrows(ResponseStatusException.class, () ->
        teacherService.deleteTeacher());

    assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    assertEquals("not a teacher", ex.getReason());
  }

  @Test
  void whenUserIsTeacher_deleteTeacher_RemovesTeacherAndResetsRole() {
    user.setTeacher(storedTeacher);
    user.setRole(Role.ROLE_TEACHER);
    when(userUtils.currentUser()).thenReturn(user);
    when(userRepository.save(user)).thenReturn(user);

    teacherService.deleteTeacher();

    verify(userRepository).save(user);
    assertNull(user.getTeacher());
    assertEquals(Role.ROLE_STUDENT, user.getRole());
  }
  //endregion

  //region getTeacherById() tests
  @Test
  void whenTeacherNotFound_getTeacherById_ThrowsNotFound() {
    when(teacherRepository.findById(5L)).thenReturn(Optional.empty());

    var ex = assertThrows(ResponseStatusException.class, () ->
        teacherService.getTeacherById(5L));

    assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    assertEquals("Teacher not found or user is not a teacher", ex.getReason());
  }

  @Test
  void whenTeacherFound_getTeacherById_ReturnsTeacher() {
    when(teacherRepository.findById(storedTeacher.getId()))
        .thenReturn(Optional.of(storedTeacher));

    Teacher result = teacherService.getTeacherById(storedTeacher.getId());

    assertEquals(storedTeacher, result);
  }
  //endregion

  //region getAllTeachers() tests
  @Test
  void whenPageRequested_getAllTeachers_ReturnsPagedTeachers() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<Teacher> page = new PageImpl<>(List.of(storedTeacher));

    when(teacherRepository.findAllByOrderByIdAsc(pageable)).thenReturn(page);

    Page<Teacher> result = teacherService.getAllTeachers(pageable);

    assertEquals(1, result.getContent().size());
    assertEquals(storedTeacher, result.getContent().get(0));
  }
  //endregion

  //region editTeacher() tests
  @Test
  void whenUserNotATeacher_editTeacher_ThrowsBadRequest() {
    // user.teacher == null
    when(userUtils.currentUser()).thenReturn(user);

    var ex = assertThrows(ResponseStatusException.class, () ->
        teacherService.editTeacher(editDto));

    assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    assertEquals("not a teacher", ex.getReason());
  }

  @Test
  void whenUserIsTeacher_editTeacher_UpdatesContactInfo() {
    // set as teacher
    user.setTeacher(storedTeacher);
    when(userUtils.currentUser()).thenReturn(user);
    when(teacherRepository.save(storedTeacher)).thenReturn(storedTeacher);

    TeacherResponse resp = teacherService.editTeacher(editDto);

    verify(teacherRepository).save(storedTeacher);
    assertEquals(editDto.getContactEmail(), storedTeacher.getContactEmail());
    assertEquals(editDto.getContactPhone(), storedTeacher.getContactPhone());
    assertEquals(editDto.getContactEmail(), resp.getContactEmail());
    assertEquals(editDto.getContactPhone(), resp.getContactPhone());
  }
  //endregion
}
