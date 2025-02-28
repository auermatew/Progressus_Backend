package hu.progressus.service;

import hu.progressus.dto.CreateUserDto;
import hu.progressus.entity.User;
import hu.progressus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService{
  private final UserRepository userRepository;

  public User createUser(CreateUserDto dto){
    User user = User.builder()
        .fullName(dto.getFullName())
        .email(dto.getEmail())
        .password(dto.getPassword())
        .profilePicture(dto.getProfilePicture())
        .dateOfBirth(dto.getDateOfBirth())
        .phoneNumber(dto.getPhoneNumber())
        .description(dto.getDescription())
        .role(dto.getRole())
        .build();
    return userRepository.save(user);
  }
}
