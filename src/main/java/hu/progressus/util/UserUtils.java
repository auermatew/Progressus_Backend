package hu.progressus.util;

import hu.progressus.cache.CachedUserService;
import hu.progressus.entity.User;
import hu.progressus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * Utility class for retrieving the currently authenticated user.
 * It uses Spring Security's SecurityContextHolder to get the user details.
 */
@RequiredArgsConstructor
@Component
public class UserUtils {
  private final UserRepository userRepository;
  private final CachedUserService cachedUserService;

  /**
   * Retrieves the currently authenticated user.
   *
   * @return the User object of the currently authenticated user
   * @throws ResponseStatusException if the user is not authenticated
   */
  public User currentUser (){
    try{
      User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      return cachedUserService.getUserByIdCached(user.getId());
    } catch (Exception e){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"User not signed in");
    }
  }
}
