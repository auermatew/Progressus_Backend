package hu.progressus.seed;

import hu.progressus.entity.Subject;
import hu.progressus.repository.SubjectRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SubjectDataSeeder implements CommandLineRunner {

  private final SubjectRepository subjectRepository;

  public SubjectDataSeeder(SubjectRepository subjectRepository) {
    this.subjectRepository = subjectRepository;
  }

  @Override
  public void run(String... args){
    List<String> defaultSubjects = List.of(
        "Matematika",
        "Fizika",
        "Kémia",
        "Biológia",
        "Történelem",
        "Földrajz",
        "Irodalom",
        "Nyelvtan",
        "Informatika",
        "Idegen nyelv"
    );

    for(String subject: defaultSubjects){
      subjectRepository.findBySubject(subject).orElseGet(()
      ->{
        Subject newSubject = new Subject();
        newSubject.setSubject(subject);
        newSubject.setVerified(true);
        return subjectRepository.save(newSubject);
      });
    }
  }
}
