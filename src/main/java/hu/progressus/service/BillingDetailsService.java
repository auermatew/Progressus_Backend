package hu.progressus.service;

import hu.progressus.dto.CreateBillingDetailsDto;
import hu.progressus.dto.EditBillingDetailsDto;
import hu.progressus.entity.BillingDetails;
import hu.progressus.entity.User;
import hu.progressus.repository.BillingDetailsRepository;
import hu.progressus.response.BillingDetailsResponse;
import hu.progressus.util.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class BillingDetailsService {
  private final UserUtils userUtils;
  private final BillingDetailsRepository billingDetailsRepository;

  public BillingDetailsResponse createBillingDetails(CreateBillingDetailsDto dto) {
    User user = userUtils.currentUser();

    if (user.getBillingDetails() != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "User already has billing details");
    }

    BillingDetails billingDetails = BillingDetails.builder()
        .user(user)
        .address_city(dto.getAddress_city())
        .address_zip(dto.getAddress_zip())
        .address_street(dto.getAddress_street())
        .address_country(dto.getAddress_country())
        .build();
    billingDetailsRepository.save(billingDetails);
    return BillingDetailsResponse.of(billingDetails);
  }

  public BillingDetailsResponse getBillingDetails() {
    User user = userUtils.currentUser();
    if (user.getBillingDetails() == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not have billing details");
    }
    return BillingDetailsResponse.of(user.getBillingDetails());
  }

  public BillingDetailsResponse getBillingDetailsByUserId(Long userId) {
    if (!billingDetailsRepository.existsByUserId(userId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with billing details");
    }
    BillingDetails billingDetails = billingDetailsRepository.findByUserId(userId)
        .orElseThrow();
    return BillingDetailsResponse.of(billingDetails);
  }

  //TODO: implement edit, delete
  public BillingDetailsResponse editBillingDetails(EditBillingDetailsDto dto){
    User user  = userUtils.currentUser();
    return null;
  }
}
