package hu.progressus.service;

import hu.progressus.entity.Subject;
import hu.progressus.repository.SubjectRepository;
import hu.progressus.util.StringFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubjectService {
  private final SubjectRepository subjectRepository;

  public Subject findOrCreateSubjectByName(String subjectName){

    Optional<Subject> existingSubject = subjectRepository.findMatch(StringFormatter.formatString(subjectName));
    if(existingSubject.isPresent()){
      return existingSubject.get();
    }

    var newSubject = Subject.builder()
        .subject(StringFormatter.formatString(subjectName))
        .isVerified(false)
        .build();
    return subjectRepository.save(newSubject);
  }

  public List<Subject> findOrCreateSubjects(List<String> subjects){
    return subjects.stream().map(this::findOrCreateSubjectByName).toList();
  }

}
