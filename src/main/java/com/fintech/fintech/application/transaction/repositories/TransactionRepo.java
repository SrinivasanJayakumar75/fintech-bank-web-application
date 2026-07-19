package com.fintech.fintech.application.transaction.repositories;

import java.util.List;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fintech.fintech.application.transaction.entities.Transaction;

public interface TransactionRepo extends JpaRepository<Transaction, Long>{

    Page<Transaction> findByAccount_AccountNumber(String accountNumber, Pageable pageable);
    List<Transaction> findByAccount_AccountNumber(String accountNumber);
}
