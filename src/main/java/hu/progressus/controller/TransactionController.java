package hu.progressus.controller;

import hu.progressus.response.TransactionResponse;
import hu.progressus.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/transactions")
public class TransactionController {
  private final TransactionService transactionService;

  @PostMapping("/transaction/{transactionId}/accept")
  public ResponseEntity<TransactionResponse> acceptTransaction(@PathVariable Long transactionId){
    return ResponseEntity.ok(transactionService.acceptLessonTransaction(transactionId));
  }
}
