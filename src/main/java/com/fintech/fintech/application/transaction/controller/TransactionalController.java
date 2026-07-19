package com.fintech.fintech.application.transaction.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fintech.fintech.application.res.Response;
import com.fintech.fintech.application.transaction.dtos.TransactionRequestDto;
import com.fintech.fintech.application.transaction.service.TransactionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionalController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<Response<?>> createTransaction(@RequestBody @Valid TransactionRequestDto request){
        return ResponseEntity.ok(transactionService.createTransaction(request));
        
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<Response<?>> getTransactionsForAnAccount(
        @PathVariable String accountNumber,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "50") int size
        ){
        return ResponseEntity.ok(transactionService.getTransactionsForAnAccount(accountNumber, page, size));
    }
    
}
