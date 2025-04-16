package hu.progressus.service;

import hu.progressus.dto.CreateBillingDetailsDto;
import hu.progressus.dto.EditBillingDetailsDto;
import hu.progressus.entity.BillingDetails;
import hu.progressus.entity.User;
import hu.progressus.repository.BillingDetailsRepository;
import hu.progressus.response.BillingDetailsResponse;
import hu.progressus.util.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Manages each user's billing details: create, read and update.
 */
@Service
@RequiredArgsConstructor
public class BillingDetailsService {
  private final UserUtils userUtils;
  private final BillingDetailsRepository billingDetailsRepository;

  /**
   * Creates billing details for the currently authenticated user.
   *
   * @param dto the DTO containing address fields
   * @return a BillingDetailsResponse DTO of the saved entity
   * @throws ResponseStatusException if the user already has billing details
   */
  @CacheEvict(
      value = "userCache",
      key = "@userUtils.currentUser().id"
  )
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

  /**
   * Retrieves billing details of the current user.
   *
   * @return a BillingDetailsResponse DTO
   * @throws ResponseStatusException if none exist
   */
  public BillingDetailsResponse getBillingDetails() {
    User user = userUtils.currentUser();
    if (user.getBillingDetails() == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not have billing details");
    }
    return BillingDetailsResponse.of(user.getBillingDetails());
  }

  /**
   * Admin-only: Retrieves billing details by user ID.
   *
   * @param userId the ID of the user whose billing details to fetch
   * @return a BillingDetailsResponse DTO
   * @throws ResponseStatusException if not found
   */

  public BillingDetailsResponse getBillingDetailsByUserId(Long userId) {
    if (!billingDetailsRepository.existsByUserId(userId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with billing details");
    }
    BillingDetails billingDetails = billingDetailsRepository.findByUserId(userId)
        .orElseThrow();
    return BillingDetailsResponse.of(billingDetails);
  }

  /**
   * Edits the billing details of the current user.
   * Only non-null fields in the DTO will be updated.
   *
   * @param dto the DTO containing updated address fields
   * @return a BillingDetailsResponse DTO of the updated entity
   * @throws ResponseStatusException if billing details do not exist
   */
  @CacheEvict(
      value = "userCache",
      key = "@userUtils.currentUser().id"
  )
  public BillingDetailsResponse editBillingDetails(EditBillingDetailsDto dto){
    User user  = userUtils.currentUser();
    BillingDetails billingDetails = billingDetailsRepository.findByUserId(user.getId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Billing details not found"));

    if (dto.getAddress_city() != null) {
      billingDetails.setAddress_city(dto.getAddress_city());
    }
    if (dto.getAddress_zip() != null) {
      billingDetails.setAddress_zip(dto.getAddress_zip());
    }
    if (dto.getAddress_street() != null) {
      billingDetails.setAddress_street(dto.getAddress_street());
    }
    if (dto.getAddress_country() != null) {
      billingDetails.setAddress_country(dto.getAddress_country());
    }

    billingDetails = billingDetailsRepository.save(billingDetails);
    return BillingDetailsResponse.of(billingDetails);
  }
}
