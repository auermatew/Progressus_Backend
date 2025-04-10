package hu.progressus.dto;

import hu.progressus.validation.ValidCountry;
import lombok.Data;

@Data
public class EditBillingDetailsDto {
  private String address_city;

  private String address_zip;

  private String address_street;

  @ValidCountry
  private String address_country;
}
