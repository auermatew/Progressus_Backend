package hu.progressus.repository;

import hu.progressus.entity.BillingDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillingDetailsRepository extends JpaRepository<BillingDetails, Long> {
}
