package hu.progressus.service;

import hu.progressus.dto.EditUserDto;
import hu.progressus.entity.Teacher;
import hu.progressus.entity.User;
import hu.progressus.repository.UserRepository;
import hu.progressus.response.AuthResponse;
import hu.progressus.response.UserResponse;
import hu.progressus.util.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service class for managing user-related operations.
 * It includes methods for checking if a user exists by email or phone number,
 * retrieving user details, and editing user information.
 */
@RequiredArgsConstructor
@Service
public class UserService{
  private final UserRepository userRepository;
  private final UserUtils userUtils;
  private final PasswordEncoder passwordEncoder;

  /**
   * Checks if a user with the given email already exists.
   *
   * @param email the email to check
   * @throws ResponseStatusException if the email is already in use
   */
  public void ThrowUserEmailExists(String email){
    if (userRepository.existsUserByEmail(email)){
      throw new ResponseStatusException(HttpStatus.CONFLICT,"email already in use");
    }
  }
  /**
   * Checks if a user with the given phone number already exists.
   *
   * @param phone the phone number to check
   * @throws ResponseStatusException if the phone number is already in use
   */
  public void ThrowUserPhoneExists(String phone){
    if (userRepository.existsUserByPhoneNumber(phone)){
      throw new ResponseStatusException(HttpStatus.CONFLICT,"phone number already in use");
    }
  }

  /**
   * Retrieve a user by ID (light DTO).
   *
   * @param userId the target user ID
   * @return a lite UserResponse DTO
   */
  public UserResponse getUserById(Long userId){
    return UserResponse.ofLite(userRepository.findById(userId).orElseThrow());
  }

  /**
   * Page through all users.
   *
   * @param pageable pagination rules
   * @return a page of User entities
   */
  public Page<User> getAllUsers(Pageable pageable){
    return userRepository.findAllByOrderByIdAsc(pageable);
  }

  /**
   * Edit the current user's profile.
   * Only non-null DTO fields are applied.
   *
   * @param dto the partial-update payload
   * @return an AuthResponse DTO reflecting updated info
   * @throws ResponseStatusException on phone conflict
   */
  public AuthResponse editUser(EditUserDto dto){
    User user = userUtils.currentUser();
    ThrowUserPhoneExists(dto.getPhoneNumber());
    if (dto.getFullName() != null) {
      user.setFullName(dto.getFullName());
    }
    if (dto.getPassword() != null) {
      user.setPassword(passwordEncoder.encode(dto.getPassword()));
    }
    if (dto.getPhoneNumber() != null) {
      user.setPhoneNumber(dto.getPhoneNumber());
    }
    if (dto.getDescription() != null) {
      user.setDescription(dto.getDescription());
    }
    user = userRepository.save(user);
    return AuthResponse.of(user);
  }
}
