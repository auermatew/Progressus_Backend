package hu.progressus.repository;

import hu.progressus.entity.UserInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInterestRepository extends JpaRepository<UserInterest,Long> {
}
