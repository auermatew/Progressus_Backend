package hu.progressus.util;

import hu.progressus.entity.Subject;
import hu.progressus.entity.Teacher;
import hu.progressus.entity.TeacherClassSubject;

import java.util.List;

public class SubjectUtils {

  public static List<Subject> getSubjectsOfTeacher(Teacher teacher) {
    return teacher.getTeacherClasses().stream()
        .flatMap(teacherClass -> teacherClass.getSubjects().stream())
        .map(TeacherClassSubject::getSubject)
        .distinct()
        .toList();
  }
}