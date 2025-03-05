package hu.progressus.service;

import hu.progressus.repository.UserRepository;
import hu.progressus.response.AuthResponse;
import hu.progressus.util.UserUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
public class UserService{
  private final UserRepository userRepository;

  public void ThrowUserEmailExists(String email){
    if (userRepository.existsUserByEmail(email)){
      throw new ResponseStatusException(HttpStatus.CONFLICT,"email already in use");
    }
  }

 /* public AuthResponse edit(HttpServletRequest request){
    return AuthResponse.of(UserUtils.currentUser());
  } */
}
