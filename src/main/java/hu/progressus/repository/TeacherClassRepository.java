package hu.progressus.repository;

import hu.progressus.entity.TeacherClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherClassRepository extends JpaRepository<TeacherClass,Long> {

  Optional<TeacherClass> findTeacherClassByIdAndTeacher_User_Id(Long id, Long teacherUserId);

  List<TeacherClass> findAllByTeacher_Id(Long teacherId);

  boolean existsByTeacherId(Long teacherId);
}
