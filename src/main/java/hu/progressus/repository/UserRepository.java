package hu.progressus.repository;

import hu.progressus.entity.Teacher;
import hu.progressus.entity.User;
import hu.progressus.response.UserResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

  boolean existsUserByEmail(@NotNull String email);

  Optional<User> findUserByEmail(@NotNull String email);

  Page<User> findAllByOrderByIdAsc(Pageable pageable);

  boolean existsUserByPhoneNumber(String phone);
}
