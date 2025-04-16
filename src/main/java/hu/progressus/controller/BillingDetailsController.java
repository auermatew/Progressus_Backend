package hu.progressus.controller;

import hu.progressus.dto.CreateBillingDetailsDto;
import hu.progressus.dto.EditBillingDetailsDto;
import hu.progressus.response.BillingDetailsResponse;
import hu.progressus.service.BillingDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/billing-details")
public class BillingDetailsController {
  private final BillingDetailsService billingDetailsService;

  @PostMapping("/create")
  @Operation(summary = "Create billing details", description = "Create billing details.")
  public ResponseEntity<BillingDetailsResponse> createBillingDetails(@Valid @RequestBody CreateBillingDetailsDto dto) {
    return ResponseEntity.ok(billingDetailsService.createBillingDetails(dto));
  }

  @GetMapping("/user")
  @Operation(summary = "Get billing details for the user", description = "Get billing details for the current user.")
  public ResponseEntity<BillingDetailsResponse> getBillingDetails() {
    return ResponseEntity.ok(billingDetailsService.getBillingDetails());
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/{userId}")
  @Operation(summary = "Get billing details by user ID", description = "Get billing details by user ID. The user must be admin to access this endpoint.")
  public ResponseEntity<BillingDetailsResponse> getBillingDetailsByUserId(@PathVariable Long userId) {
    return ResponseEntity.ok(billingDetailsService.getBillingDetailsByUserId(userId));
  }

  @PatchMapping("/edit")
  @Operation(summary = "Edit billing details", description = "Edit billing details.")
  public ResponseEntity<BillingDetailsResponse> editBillingDetails(@Valid @RequestBody EditBillingDetailsDto dto) {
    return ResponseEntity.ok(billingDetailsService.editBillingDetails(dto));
  }
}
