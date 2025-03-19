package hu.progressus.repository;

import hu.progressus.entity.TeacherClassSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherClassSubjectRepository extends JpaRepository<TeacherClassSubject, Long> {

  @Query("SELECT tcs FROM TeacherClassSubject tcs WHERE tcs.teacherClass.id = :teacherClassId AND tcs.subject.id IN :subjectIds")
  List<TeacherClassSubject> findAllByTeacherClassIdAndSubjectIdIn(
      @Param("teacherClassId") Long teacherClassId,
      @Param("subjectIds") List<Long> subjectIds
  );

  boolean existsBySubjectId(Long subjectId);
}

