package hu.progressus.repository;

import hu.progressus.entity.BillingDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BillingDetailsRepository extends JpaRepository<BillingDetails, Long> {

  boolean existsByUserId(Long userId);

  Optional<BillingDetails> findByUserId(Long userId);
}
