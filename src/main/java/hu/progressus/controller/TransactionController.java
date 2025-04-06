package hu.progressus.controller;

import hu.progressus.entity.Transaction;
import hu.progressus.response.TransactionResponse;
import hu.progressus.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/transactions")
public class TransactionController {
  private final TransactionService transactionService;

  @PreAuthorize("hasRole('STUDENT')")
  @PostMapping("/transaction/{transactionId}/accept")
  public ResponseEntity<TransactionResponse> acceptTransaction(@PathVariable Long transactionId){
    return ResponseEntity.ok(transactionService.acceptLessonTransaction(transactionId));
  }

  @GetMapping("/incoming")
  public ResponseEntity<Page<TransactionResponse>> getIncomingTransactions(@PageableDefault (size = 15)Pageable pageable){
    Page<Transaction> transactionPage = transactionService.getIncomingTransactions(pageable);

    Page<TransactionResponse> transactionResponses = transactionPage.map(TransactionResponse::of);
    return ResponseEntity.ok(transactionResponses);
  }

  @GetMapping("/outgoing")
  public ResponseEntity<Page<TransactionResponse>> getOutgoingTransactions(@PageableDefault (size = 15)Pageable pageable){
    Page<Transaction> transactionPage = transactionService.getOutgoingTransactions(pageable);

    Page<TransactionResponse> transactionResponses = transactionPage.map(TransactionResponse::of);
    return ResponseEntity.ok(transactionResponses);
  }
}
