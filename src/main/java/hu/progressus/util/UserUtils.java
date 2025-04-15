package hu.progressus.util;

import hu.progressus.entity.User;
import hu.progressus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Component
public class UserUtils {
  private final UserRepository userRepository;

  public User currentUser (){
    try{
      User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      return userRepository.findById(user.getId()).orElseThrow();
    } catch (Exception e){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"User not signed in");
    }
  }
}
