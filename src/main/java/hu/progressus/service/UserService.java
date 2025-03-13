package hu.progressus.service;

import hu.progressus.dto.EditUserDto;
import hu.progressus.entity.User;
import hu.progressus.repository.UserRepository;
import hu.progressus.response.AuthResponse;
import hu.progressus.util.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
public class UserService{
  private final UserRepository userRepository;
  private final UserUtils userUtils;
  private final PasswordEncoder passwordEncoder;

  public void ThrowUserEmailExists(String email){
    if (userRepository.existsUserByEmail(email)){
      throw new ResponseStatusException(HttpStatus.CONFLICT,"email already in use");
    }
  }
  public void ThrowUserPhoneExists(String phone){
    if (userRepository.existsUserByPhoneNumber(phone)){
      throw new ResponseStatusException(HttpStatus.CONFLICT,"phone number already in use");
    }
  }

  public AuthResponse editUser(EditUserDto dto){
    User user = userUtils.currentUser();
    if (dto.getFullName() != null) {
      user.setFullName(dto.getFullName());
    }
    if (dto.getProfilePicture() != null) {
      user.setProfilePicture(dto.getProfilePicture());
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
