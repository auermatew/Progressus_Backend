package hu.progressus.util;

import hu.progressus.entity.User;
import hu.progressus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserUtils {
  private final UserRepository userRepository;

  public User currentUser (){
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return userRepository.findById(user.getId()).orElseThrow();
  }

}
