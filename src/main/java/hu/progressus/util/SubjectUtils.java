package hu.progressus.util;

import hu.progressus.entity.Subject;
import hu.progressus.entity.Teacher;
import hu.progressus.entity.TeacherClassSubject;

import java.util.List;

/**
 * Utility class for getting subjects of a teacher.
 */
public class SubjectUtils {

  /**
   * Retrieves a list of distinct subjects associated with a given teacher.
   *
   * @param teacher the teacher whose subjects are to be retrieved
   * @return a list of distinct subjects associated with the teacher
   */
  public static List<Subject> getSubjectsOfTeacher(Teacher teacher) {
    return teacher.getTeacherClasses().stream()
        .flatMap(teacherClass -> teacherClass.getSubjects().stream())
        .map(TeacherClassSubject::getSubject)
        .distinct()
        .toList();
  }
}