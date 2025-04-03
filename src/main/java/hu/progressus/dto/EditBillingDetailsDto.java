package hu.progressus.dto;

import lombok.Data;

@Data
public class EditBillingDetailsDto {
  private String address_city;

  private String address_zip;

  private String address_street;

  private String address_country;
}
