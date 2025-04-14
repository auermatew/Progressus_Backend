package hu.progressus.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Table(name = "images")
@Entity
@Data
@ToString(exclude = "user")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Image {
 @Id
 @GeneratedValue
  private Long id;

 private String url;

 @ManyToOne
 private User user;

 private String key;
}
