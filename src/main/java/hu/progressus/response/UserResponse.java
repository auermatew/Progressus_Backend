package hu.progressus.response;

import hu.progressus.entity.User;
import lombok.Data;

@Data
public class UserResponse {
  private Long id;

  private String fullName;

  private String email;

  private String phoneNumber;

  private String description;

  private String profilePicture;

  protected UserResponse(User user){
    this.id = user.getId();
    this.fullName = user.getFullName();
    this.phoneNumber = user.getPhoneNumber();
    this.description = user.getDescription();
    this.profilePicture = user.getProfilePicture();
  }

  public static UserResponse of(User user){
    return new UserResponse(user);
  }

  public static UserResponse ofLite(User user){
    UserResponse response = new UserResponse(user);
    response.setEmail(null);
    response.setPhoneNumber(null);
    return response;
  }
}
