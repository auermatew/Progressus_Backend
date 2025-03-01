package hu.progressus.service;

import hu.progressus.repository.UserRepository;
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

}
