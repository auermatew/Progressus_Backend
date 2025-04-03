package hu.progressus.response;

import hu.progressus.entity.BillingDetails;
import lombok.Data;

@Data
public class BillingDetailsResponse {

  private Long id;

  //private UserResponse user;

  private String address_city;

  private String address_zip;

  private String address_street;

  private String address_country;

  protected BillingDetailsResponse(BillingDetails billingDetails) {
    this.id = billingDetails.getId();
    //this.user = UserResponse.of(billingDetails.getUser());
    this.address_city = billingDetails.getAddress_city();
    this.address_zip = billingDetails.getAddress_zip();
    this.address_street = billingDetails.getAddress_street();
    this.address_country = billingDetails.getAddress_country();
  }

  public static BillingDetailsResponse of(BillingDetails billingDetails){
    return new BillingDetailsResponse(billingDetails);
  }

}
