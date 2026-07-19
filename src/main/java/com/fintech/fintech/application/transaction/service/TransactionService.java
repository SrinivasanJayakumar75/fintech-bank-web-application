package com.fintech.fintech.application.transaction.service;

import java.util.List;

import com.fintech.fintech.application.res.Response;
import com.fintech.fintech.application.transaction.dtos.TransactionDto;
import com.fintech.fintech.application.transaction.dtos.TransactionRequestDto;

public interface TransactionService {

    Response<?>createTransaction(TransactionRequestDto transactionRequest);

    Response<List<TransactionDto>> getTransactionsForAnAccount(String accountNumber, int page, int size);

    
}
