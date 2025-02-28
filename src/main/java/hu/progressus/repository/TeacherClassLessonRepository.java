package hu.progressus.repository;

import hu.progressus.entity.TeacherClassLesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherClassLessonRepository extends JpaRepository<TeacherClassLesson, Long> {
}
