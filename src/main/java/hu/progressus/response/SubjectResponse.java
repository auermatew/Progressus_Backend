package hu.progressus.response;

import hu.progressus.entity.Subject;
import lombok.Data;

@Data
public class SubjectResponse {

  private Long id;

  private String subject;

  private boolean isVerified;

  protected SubjectResponse(Subject subject) {
    this.id = subject.getId();
    this.subject = subject.getSubject();
    this.isVerified = subject.isVerified();
  }

  public static SubjectResponse of(Subject subject) {
    return new SubjectResponse(subject);
  }
}