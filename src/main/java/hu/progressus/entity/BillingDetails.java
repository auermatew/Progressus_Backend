package hu.progressus.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "billing_details")
public class BillingDetails {
  @Id
  @GeneratedValue
  private Long id;


  @OneToOne(fetch = FetchType.LAZY)
  private User user;

  private String address_city;

  private String address_zip;

  private String address_street;

  private String address_country;

  @OneToMany(mappedBy = "billingDetails",  cascade = CascadeType.ALL)
  private List<Transaction> transaction;

}
