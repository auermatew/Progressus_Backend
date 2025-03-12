package hu.progressus.repository;

import hu.progressus.entity.User;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

  boolean existsUserByEmail(@NotNull String email);

  Optional<User> findUserByEmail(@NotNull String email);

  boolean existsUserByPhoneNumber(String phone);
}
