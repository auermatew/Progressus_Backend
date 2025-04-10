package hu.progressus.repository;

import hu.progressus.entity.Teacher;
import hu.progressus.entity.User;
import hu.progressus.response.UserResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

  boolean existsUserByEmail(@NotNull String email);

  Optional<User> findUserByEmail(@NotNull String email);

  Page<User> findAllByOrderByIdAsc(Pageable pageable);

  boolean existsUserByPhoneNumber(String phone);

  @Modifying
  @Query("UPDATE User u SET u.balance = u.balance - :amount WHERE u.id = :fromUserId AND u.balance >= :amount")
  int deductBalance(@Param("fromUserId") Long fromUserId, @Param("amount") int amount);

  @Modifying
  @Query("UPDATE User u SET u.balance = u.balance + :amount WHERE u.id = :toUserId")
  int creditBalance(@Param("toUserId") Long toUserId, @Param("amount") int amount);

  @Modifying
  @Query("UPDATE User u SET u.profileImg.id = :imageId WHERE u.id = :userId")
  int updateProfilePicture(@Param("userId") Long userId, @Param("imageId") Long imageId);
}
