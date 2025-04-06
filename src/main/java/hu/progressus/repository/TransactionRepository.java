package hu.progressus.repository;

import hu.progressus.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {

  Page<Transaction> findAllByBillingDetails_User_IdOrderByDateDesc(Long userId, Pageable pageable);

  Page<Transaction> findAllByLessonReservation_TeacherClassLesson_TeacherClass_Teacher_User_IdOrderByDateDesc(Long userId, Pageable pageable);
}
