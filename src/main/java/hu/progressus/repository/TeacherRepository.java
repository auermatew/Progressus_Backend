package hu.progressus.repository;

import hu.progressus.entity.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher,Long> {

  Page<Teacher> findAllByOrderByIdAsc(Pageable pageable);
}
