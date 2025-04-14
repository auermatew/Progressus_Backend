package hu.progressus.repository;

import hu.progressus.entity.Subject;
import hu.progressus.entity.TeacherClassSubject;
import hu.progressus.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
  Optional<Subject> findBySubject(String subject);

  @Query("SELECT s FROM Subject s WHERE s.subject ILIKE :input")
  Optional<Subject> findMatch(@Param("input") String input);

  @Query(value = "SELECT * FROM subjects s WHERE s.subject IN :input", nativeQuery = true)
  List<Subject> findMatchesFromList(@Param("input") List<String> input);

  Page<Subject> findAllByOrderByIdAsc(Pageable pageable);

  boolean existsBySubject(String subject);

  @Query("""
    SELECT s.id FROM Subject s
    WHERE s.isVerified = false
      AND s.id IN :subjectIds
      AND NOT EXISTS (
        SELECT 1 FROM TeacherClassSubject tcs
        WHERE tcs.subject.id = s.id
      )
""")
  List<Long> findUnusedUnverifiedSubjectIds(@Param("subjectIds") List<Long> subjectIds);

  void deleteByIdIn(List<Long> ids);

}