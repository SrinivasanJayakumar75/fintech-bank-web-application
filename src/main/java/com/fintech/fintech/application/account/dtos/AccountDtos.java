package com.fintech.fintech.application.account.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fintech.fintech.application.auth_users.dtos.UserDto;
import com.fintech.fintech.application.enums.AccountStatus;
import com.fintech.fintech.application.enums.AccountType;
import com.fintech.fintech.application.enums.Currency;
import com.fintech.fintech.application.transaction.dtos.TransactionDto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class AccountDtos {
 
    private Long id;

    private String accountNumber;
 
    private BigDecimal balance;

    private AccountType accountType;
  
    @JsonBackReference
    private UserDto user;

    private Currency currency;

    private AccountStatus status;

    @JsonManagedReference
    private List<TransactionDto> transactions;

    private LocalDateTime closedAt;

    private LocalDateTime createdAt;
    private LocalDateTime upDatedAt;
    
}
