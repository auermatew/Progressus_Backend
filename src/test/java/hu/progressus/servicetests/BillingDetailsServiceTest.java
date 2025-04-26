package hu.progressus.servicetests;

import hu.progressus.dto.CreateBillingDetailsDto;
import hu.progressus.dto.EditBillingDetailsDto;
import hu.progressus.entity.BillingDetails;
import hu.progressus.entity.User;
import hu.progressus.repository.BillingDetailsRepository;
import hu.progressus.response.BillingDetailsResponse;
import hu.progressus.service.BillingDetailsService;
import hu.progressus.util.UserUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BillingDetailsServiceTest {
  @Mock UserUtils userUtils;
  @Mock BillingDetailsRepository billingDetailsRepository;

  @InjectMocks BillingDetailsService billingDetailsService;

  User user;
  BillingDetails details;
  CreateBillingDetailsDto createDto;
  EditBillingDetailsDto editDto;

  @BeforeEach
  void setup() {
    user = User.builder().id(1L).build();
    user.setBillingDetails(null);

    createDto = new CreateBillingDetailsDto();
    createDto.setAddress_city("City");
    createDto.setAddress_zip("12345");
    createDto.setAddress_street("Main St");
    createDto.setAddress_country("Country");

    editDto = new EditBillingDetailsDto();
    editDto.setAddress_city("New City");
    editDto.setAddress_street("Second St");

    details = BillingDetails.builder()
        .id(10L)
        .user(user)
        .address_city("City")
        .address_zip("12345")
        .address_street("Main St")
        .address_country("Country")
        .build();
    user.setBillingDetails(details);
  }

  //region createBillingDetails() tests
  @Test
  void whenBillingDetailsAlreadyExists_createBillingDetails_ThrowsConflict() {
    user.setBillingDetails(details);
    when(userUtils.currentUser()).thenReturn(user);

    var ex = assertThrows(ResponseStatusException.class, () ->
        billingDetailsService.createBillingDetails(createDto));

    assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    assertEquals("User already has billing details", ex.getReason());
  }

  @Test
  void whenNoExistingBillingDetails_createBillingDetails_SavesAndReturnsResponse() {
    user.setBillingDetails(null);
    when(userUtils.currentUser()).thenReturn(user);
    when(billingDetailsRepository.save(any(BillingDetails.class))).thenAnswer(inv -> {
      BillingDetails bd = inv.getArgument(0);
      bd.setId(10L);
      return bd;
    });

    BillingDetailsResponse resp = billingDetailsService.createBillingDetails(createDto);

    verify(billingDetailsRepository).save(any(BillingDetails.class));
    assertEquals(10L, resp.getId());
    assertEquals("City", resp.getAddress_city());
    assertEquals("12345", resp.getAddress_zip());
    assertEquals("Main St", resp.getAddress_street());
    assertEquals("Country", resp.getAddress_country());
  }
  //endregion

  //region getBillingDetails() tests
  @Test
  void whenNoBillingDetails_getBillingDetails_ThrowsNotFound() {
    user.setBillingDetails(null);
    when(userUtils.currentUser()).thenReturn(user);

    var ex = assertThrows(ResponseStatusException.class, () ->
        billingDetailsService.getBillingDetails());

    assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    assertEquals("User does not have billing details", ex.getReason());
  }

  @Test
  void whenBillingDetailsExists_getBillingDetails_ReturnsResponse() {
    user.setBillingDetails(details);
    when(userUtils.currentUser()).thenReturn(user);

    BillingDetailsResponse resp = billingDetailsService.getBillingDetails();

    assertEquals(10L, resp.getId());
    assertEquals("City", resp.getAddress_city());
  }
  //endregion

  //region getBillingDetailsByUserId() tests
  @Test
  void whenNoBillingDetailsForUser_getBillingDetailsByUserId_ThrowsNotFound() {
    when(billingDetailsRepository.existsByUserId(2L)).thenReturn(false);

    var ex = assertThrows(ResponseStatusException.class, () ->
        billingDetailsService.getBillingDetailsByUserId(2L));

    assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    assertEquals("User not found with billing details", ex.getReason());
  }

  @Test
  void whenBillingDetailsExistForUser_getBillingDetailsByUserId_ReturnsResponse() {
    when(billingDetailsRepository.existsByUserId(1L)).thenReturn(true);
    when(billingDetailsRepository.findByUserId(1L)).thenReturn(Optional.of(details));

    BillingDetailsResponse resp = billingDetailsService.getBillingDetailsByUserId(1L);

    assertEquals(10L, resp.getId());
    assertEquals("City", resp.getAddress_city());
  }
  //endregion

  //region editBillingDetails() tests
  @Test
  void whenNoBillingDetails_editBillingDetails_ThrowsNotFound() {
    user.setBillingDetails(null);
    when(userUtils.currentUser()).thenReturn(user);
    when(billingDetailsRepository.findByUserId(1L)).thenReturn(Optional.empty());

    var ex = assertThrows(ResponseStatusException.class, () ->
        billingDetailsService.editBillingDetails(editDto));

    assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    assertEquals("Billing details not found", ex.getReason());
  }

  @Test
  void whenValidEdit_editBillingDetails_UpdatesFields() {
    when(userUtils.currentUser()).thenReturn(user);
    when(billingDetailsRepository.findByUserId(1L)).thenReturn(Optional.of(details));
    when(billingDetailsRepository.save(details)).thenReturn(details);

    BillingDetailsResponse resp = billingDetailsService.editBillingDetails(editDto);

    verify(billingDetailsRepository).save(details);
    // updated fields
    assertEquals("New City", resp.getAddress_city());
    assertEquals("Second St", resp.getAddress_street());
    // unchanged fields
    assertEquals("12345", resp.getAddress_zip());
    assertEquals("Country", resp.getAddress_country());
  }
  //endregion
}
