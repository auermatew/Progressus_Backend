package hu.progressus.dto;

import hu.progressus.validation.ValidCountry;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateBillingDetailsDto {

  @NotBlank
  private String address_city;

  @NotBlank
  private String address_zip;

  @NotBlank
  private String address_street;

  @NotBlank
  @ValidCountry //The address_country must be a valid ISO country code!!
  private String address_country;
}
