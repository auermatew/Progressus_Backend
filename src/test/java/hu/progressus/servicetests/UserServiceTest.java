package hu.progressus.servicetests;

import hu.progressus.dto.EditUserDto;
import hu.progressus.entity.User;
import hu.progressus.repository.UserRepository;
import hu.progressus.response.AuthResponse;
import hu.progressus.response.UserResponse;
import hu.progressus.service.UserService;
import hu.progressus.util.UserUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
  @Mock UserRepository userRepository;
  @Mock UserUtils userUtils;
  @Mock PasswordEncoder passwordEncoder;

  @InjectMocks UserService userService;

  User user;
  EditUserDto editDto;

  @BeforeEach
  void setup() {
    user = User.builder()
        .id(1L)
        .email("u@example.com")
        .fullName("Old Name")
        .password("oldpwd")
        .phoneNumber("111")
        .description("old desc")
        .build();

    editDto = new EditUserDto();
    editDto.setFullName("New Name");
    editDto.setPassword("newpwd");
    editDto.setPhoneNumber("222");
    editDto.setDescription("new desc");
  }

  //region ThrowUserEmailExists() tests
  @Test
  void whenEmailExists_ThrowUserEmailExists_ThrowsConflict() {
    when(userRepository.existsUserByEmail("u@example.com")).thenReturn(true);

    var ex = assertThrows(ResponseStatusException.class, () ->
        userService.ThrowUserEmailExists("u@example.com"));

    assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    assertEquals("email already in use", ex.getReason());
  }

  @Test
  void whenEmailNotExists_ThrowUserEmailExists_DoesNotThrow() {
    when(userRepository.existsUserByEmail("u@example.com")).thenReturn(false);

    assertDoesNotThrow(() -> userService.ThrowUserEmailExists("u@example.com"));
  }
  //endregion

  //region ThrowUserPhoneExists() tests
  @Test
  void whenPhoneExists_ThrowUserPhoneExists_ThrowsConflict() {
    when(userRepository.existsUserByPhoneNumber("111")).thenReturn(true);

    var ex = assertThrows(ResponseStatusException.class, () ->
        userService.ThrowUserPhoneExists("111"));

    assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    assertEquals("phone number already in use", ex.getReason());
  }

  @Test
  void whenPhoneNotExists_ThrowUserPhoneExists_DoesNotThrow() {
    when(userRepository.existsUserByPhoneNumber("111")).thenReturn(false);

    assertDoesNotThrow(() -> userService.ThrowUserPhoneExists("111"));
  }
  //endregion

  //region getUserById() tests
  @Test
  void whenUserNotFound_getUserById_ThrowsNotFound() {
    when(userRepository.findById(2L)).thenReturn(Optional.empty());

    var ex = assertThrows(ResponseStatusException.class, () ->
        userService.getUserById(2L));

    assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    assertEquals("User not found", ex.getReason());
  }

  @Test
  void whenUserFound_getUserById_ReturnsResponse() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    UserResponse resp = userService.getUserById(1L);

    assertEquals(user.getId(), resp.getId());
    assertEquals(user.getFullName(), resp.getFullName());
  }
  //endregion

  //region getAllUsers() tests
  @Test
  void whenPageRequested_getAllUsers_ReturnsPagedUsers() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<User> page = new PageImpl<>(List.of(user));
    when(userRepository.findAllByOrderByIdAsc(pageable)).thenReturn(page);

    Page<User> result = userService.getAllUsers(pageable);

    assertEquals(page, result);
  }
  //endregion

  //region editUser() tests
  @Test
  void whenPhoneConflict_editUser_ThrowsConflict() {
    when(userUtils.currentUser()).thenReturn(user);
    when(userRepository.existsUserByPhoneNumber("222")).thenReturn(true);

    var ex = assertThrows(ResponseStatusException.class, () ->
        userService.editUser(editDto));

    assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    assertEquals("phone number already in use", ex.getReason());
  }

  @Test
  void whenValidDto_editUser_UpdatesAndReturnsAuthResponse() {
    when(userUtils.currentUser()).thenReturn(user);
    when(userRepository.existsUserByPhoneNumber("222")).thenReturn(false);
    when(passwordEncoder.encode("newpwd")).thenReturn("encodedPwd");
    when(userRepository.save(user)).thenReturn(user);

    AuthResponse resp = userService.editUser(editDto);

    verify(userRepository).save(user);
    assertEquals("New Name", user.getFullName());
    assertEquals("encodedPwd", user.getPassword());
    assertEquals("222", user.getPhoneNumber());
    assertEquals("new desc", user.getDescription());
    assertEquals(user.getId(), resp.getId());
    assertEquals(user.getFullName(), resp.getFullName());
  }
  //endregion
}
