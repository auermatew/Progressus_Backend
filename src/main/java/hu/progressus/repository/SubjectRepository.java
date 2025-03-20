package hu.progressus.repository;

import hu.progressus.entity.Subject;
import hu.progressus.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.nio.channels.FileChannel;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
  Optional<Subject> findBySubject(String subject);

  @Query("SELECT s FROM Subject s WHERE s.subject ILIKE :input")
  Optional<Subject> findMatch(@Param("input") String input);

  Page<Subject> findAllByOrderByIdAsc(Pageable pageable);

  boolean existsBySubject(String subject);
}
