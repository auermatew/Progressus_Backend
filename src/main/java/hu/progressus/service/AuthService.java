package hu.progressus.service;

import hu.progressus.dto.CreateUserDto;
import hu.progressus.dto.LoginDto;
import hu.progressus.entity.User;
import hu.progressus.repository.UserRepository;
import hu.progressus.response.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuthService {
  private final PasswordEncoder passwordEncoder;
  private final UserService userService;
  private final UserRepository userRepository;


  public AuthResponse register(CreateUserDto dto){
    userService.ThrowUserEmailExists(dto.getEmail());
    User user = User.builder()
        .fullName(dto.getFullName())
        .email(dto.getEmail())
        .password(passwordEncoder.encode(dto.getPassword()))
        .profilePicture(dto.getProfilePicture())
        .dateOfBirth(dto.getDateOfBirth())
        .phoneNumber(dto.getPhoneNumber())
        .description(dto.getDescription())
        .role(dto.getRole())
        .build();
    user = userRepository.save(user);

    return AuthResponse.of(user);
  }

  public AuthResponse login(LoginDto dto){
    Optional<User> optionalUser = userRepository.findUserByEmail(dto.getEmail());
    if (optionalUser.isEmpty()){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid email or password");
    }
    User user = optionalUser.get();

    if (!passwordEncoder.matches(dto.getPassword(),user.getPassword())){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid email or password");
    }

    return AuthResponse.of(user);
  }

}
