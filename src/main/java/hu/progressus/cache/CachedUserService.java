package hu.progressus.cache;

import hu.progressus.entity.User;
import hu.progressus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * Caches user data to improve performance and reduce database load.
 */
@Component
@RequiredArgsConstructor
public class CachedUserService {
  private final UserRepository userRepository;

  /**
   * Retrieves a user by ID from the cache or database.
   *
   * @param userId the ID of the user
   * @return the User object
   * @throws ResponseStatusException if the user is not found
   */
  @Cacheable(value = "userCache", key = "#userId")
  public User getUserByIdCached(Long userId){
    return userRepository.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
  }
}
