package hu.progressus.repository;

import hu.progressus.entity.TeacherSubjectTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherSubjectTagRepository extends JpaRepository<TeacherSubjectTag, Long> {
}
