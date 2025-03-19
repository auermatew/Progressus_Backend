package hu.progressus.response;

import hu.progressus.entity.Teacher;
import hu.progressus.entity.User;
import hu.progressus.enums.Role;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AuthResponse {

  private Long id;

  private String fullName;

  private String email;

  private String profilePicture;

  private LocalDate dateOfBirth;

  private String phoneNumber;

  private String description;

  private Integer balance;

  private Role role;

  private TeacherResponseLite teacher;

  protected AuthResponse (User user){
    this.id = user.getId();
    this.fullName = user.getFullName();
    this.email = user.getEmail();
    this.profilePicture = user.getProfilePicture();
    this.dateOfBirth = user.getDateOfBirth();
    this.phoneNumber = user.getPhoneNumber();
    this.description = user.getDescription();
    this.balance = user.getBalance();
    this.role = user.getRole();

    if (user.getTeacher() != null) {
      this.teacher = TeacherResponseLite.of(user.getTeacher());
    }
  }


  public static AuthResponse of(User user){
    return new AuthResponse(user);
  }
}
