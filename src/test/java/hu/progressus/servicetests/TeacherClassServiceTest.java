package hu.progressus.servicetests;

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
import hu.progressus.service.SubjectService;
import hu.progressus.service.TeacherClassService;
import hu.progressus.util.UserUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeacherClassServiceTest {
  @Mock TeacherClassRepository teacherClassRepository;
  @Mock TeacherClassSubjectRepository teacherClassSubjectRepository;
  @Mock UserUtils userUtils;
  @Mock SubjectService subjectService;
  @Mock SubjectRepository subjectRepository;

  @InjectMocks TeacherClassService teacherClassService;

  User user;
  TeacherClass teacherClass;

  @BeforeEach
  void setup() {
    user = User.builder().id(1L).build();
    var teacher = hu.progressus.entity.Teacher.builder().id(10L).user(user).build();
    user.setTeacher(teacher);

    teacherClass = TeacherClass.builder()
        .id(100L)
        .teacher(teacher)
        .title("Old Title")
        .description("Old Desc")
        .price(50)
        .subjects(new ArrayList<>())
        .build();
    teacherClass.setSubjects(new ArrayList<>());
  }

  //region createTeacherClass() tests
  @Test
  void whenNoSubjects_createTeacherClass_returnsResponse() {
    CreateTeacherClassDto dto = new CreateTeacherClassDto();
    dto.setTitle("New Class");
    dto.setDescription("Desc");
    dto.setPrice(75);
    dto.setSubjects(List.of());  // empty

    when(userUtils.currentUser()).thenReturn(user);
    when(teacherClassRepository.save(any(TeacherClass.class))).thenAnswer(i -> {
      TeacherClass arg = i.getArgument(0);
      arg.setId(200L);
      arg.setSubjects(new ArrayList<>());
      return arg;
    });

    TeacherClassResponse resp = teacherClassService.createTeacherClass(dto);

    verify(teacherClassRepository).save(any());
    verifyNoInteractions(subjectService);
    assertEquals(200L, resp.getId());
    assertEquals("New Class", resp.getTitle());
  }

  @Test
  void whenSubjectsProvided_createTeacherClass_savesAssociations() {
    CreateTeacherClassDto dto = new CreateTeacherClassDto();
    dto.setTitle("Class A");
    dto.setDescription("Desc A");
    dto.setPrice(0);
    dto.setSubjects(List.of("Math", "Science"));

    when(userUtils.currentUser()).thenReturn(user);
    when(teacherClassRepository.save(any(TeacherClass.class))).thenAnswer(inv -> {
      TeacherClass arg = inv.getArgument(0);
      arg.setId(teacherClass.getId());
      arg.setSubjects(new ArrayList<>());
      return arg;
    });

    Subject s1 = Subject.builder().id(1L).subject("Math").build();
    Subject s2 = Subject.builder().id(2L).subject("Science").build();
    when(subjectService.findOrCreateSubjects(dto.getSubjects())).thenReturn(List.of(s1, s2));
    when(teacherClassSubjectRepository.save(any(TeacherClassSubject.class)))
        .thenAnswer(i -> i.getArgument(0));

    TeacherClassResponse resp = teacherClassService.createTeacherClass(dto);

    verify(subjectService).findOrCreateSubjects(dto.getSubjects());
    verify(teacherClassSubjectRepository, times(2)).save(any());
    assertEquals(teacherClass.getId(), resp.getId());
  }
  //endregion

  //region editTeacherClass() tests
  @Test
  void whenClassNotFound_editTeacherClass_throwsNotFound() {
    when(userUtils.currentUser()).thenReturn(user);
    when(teacherClassRepository.findTeacherClassByIdAndTeacher_User_Id(999L, user.getId()))
        .thenReturn(Optional.empty());

    var ex = assertThrows(ResponseStatusException.class, () ->
        teacherClassService.editTeacherClass(999L, new EditTeacherClassDto()));

    assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
  }

  @Test
  void whenValidInput_editTeacherClass_updatesFields() {
    EditTeacherClassDto dto = new EditTeacherClassDto();
    dto.setTitle("New Title");
    dto.setDescription("New Desc");
    dto.setPrice(123);

    when(userUtils.currentUser()).thenReturn(user);
    when(teacherClassRepository.findTeacherClassByIdAndTeacher_User_Id(teacherClass.getId(), user.getId()))
        .thenReturn(Optional.of(teacherClass));
    when(teacherClassRepository.save(teacherClass)).thenReturn(teacherClass);

    TeacherClassResponse resp = teacherClassService.editTeacherClass(teacherClass.getId(), dto);

    verify(teacherClassRepository).save(teacherClass);
    assertEquals("New Title", resp.getTitle());
    assertEquals("New Desc", resp.getDescription());
    assertEquals(123, resp.getPrice());
  }
  //endregion

  //region deleteTeacherClass() tests
  @Test
  void whenClassNotFound_deleteTeacherClass_throwsNotFound() {
    when(userUtils.currentUser()).thenReturn(user);
    when(teacherClassRepository.findTeacherClassByIdAndTeacher_User_Id(5L, user.getId()))
        .thenReturn(Optional.empty());

    var ex = assertThrows(ResponseStatusException.class, () ->
        teacherClassService.deleteTeacherClass(5L));

    assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
  }

  @Test
  void whenClassFound_deleteTeacherClass_deletesEntity() {
    when(userUtils.currentUser()).thenReturn(user);
    when(teacherClassRepository.findTeacherClassByIdAndTeacher_User_Id(teacherClass.getId(), user.getId()))
        .thenReturn(Optional.of(teacherClass));

    teacherClassService.deleteTeacherClass(teacherClass.getId());

    verify(teacherClassRepository).delete(teacherClass);
  }
  //endregion

  //region getTeacherClassesOfTeacher() tests
  @Test
  void whenClassesExist_getTeacherClassesOfTeacher_returnsResponses() {
    when(teacherClassRepository.findAllByTeacher_Id(user.getTeacher().getId()))
        .thenReturn(List.of(teacherClass));

    var list = teacherClassService.getTeacherClassesOfTeacher(user.getTeacher().getId());

    assertEquals(1, list.size());
    assertEquals(teacherClass.getTitle(), list.get(0).getTitle());
  }
  //endregion

  //region getTeacherClassById() tests
  @Test
  void whenClassNotFound_getTeacherClassById_throwsNotFound() {
    when(teacherClassRepository.findById(123L)).thenReturn(Optional.empty());

    var ex = assertThrows(ResponseStatusException.class, () ->
        teacherClassService.getTeacherClassById(123L));

    assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
  }

  @Test
  void whenClassFound_getTeacherClassById_returnsResponse() {
    when(teacherClassRepository.findById(teacherClass.getId())).thenReturn(Optional.of(teacherClass));

    TeacherClassResponse resp = teacherClassService.getTeacherClassById(teacherClass.getId());

    assertEquals(teacherClass.getId(), resp.getId());
    assertEquals(teacherClass.getTitle(), resp.getTitle());
  }
  //endregion
}
