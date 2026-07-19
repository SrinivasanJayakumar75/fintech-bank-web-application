package com.fintech.fintech.application.account.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fintech.fintech.application.account.entities.Account;

public interface AccountRepo extends JpaRepository<Account, Long>{
    Optional<Account> findByAccountNumber(String accountNumber);    
    List<Account> findByUserId(Long userId);
}
