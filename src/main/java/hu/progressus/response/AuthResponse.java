package hu.progressus.response;

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

  protected AuthResponse (User user){
    this.id = user.getId();
    this.fullName = user.getFullName();
    this.email = user.getEmail();
    this.profilePicture = user.getProfilePicture();
    this.dateOfBirth = user.getDateOfBirth();
    this.phoneNumber = getPhoneNumber();
    this.description = getDescription();
    this.balance = getBalance();
    this.role = getRole();
  }

  public static AuthResponse of(User user){
    return new AuthResponse(user);
  }
}
