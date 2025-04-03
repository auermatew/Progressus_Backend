package hu.progressus.controller;

import hu.progressus.dto.CreateBillingDetailsDto;
import hu.progressus.response.BillingDetailsResponse;
import hu.progressus.service.BillingDetailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
  public ResponseEntity<BillingDetailsResponse> createBillingDetails(@Valid @RequestBody CreateBillingDetailsDto dto) {
    return ResponseEntity.ok(billingDetailsService.createBillingDetails(dto));
  }

  @GetMapping("/user")
  public ResponseEntity<BillingDetailsResponse> getBillingDetails() {
    return ResponseEntity.ok(billingDetailsService.getBillingDetails());
  }

  @GetMapping("/{userId}")
  public ResponseEntity<BillingDetailsResponse> getBillingDetailsByUserId(@PathVariable Long userId) {
    return ResponseEntity.ok(billingDetailsService.getBillingDetailsByUserId(userId));
  }
}
