package com.fintech.fintech.application.account.service;

import java.util.List;

import com.fintech.fintech.application.account.dtos.AccountDtos;
import com.fintech.fintech.application.account.entities.Account;
import com.fintech.fintech.application.auth_users.entities.User;
import com.fintech.fintech.application.enums.AccountType;
import com.fintech.fintech.application.res.Response;

public interface AccountService {

    Account createAccount(AccountType accountType, User user);
    Response<List<AccountDtos>> getMyAccounts();
    Response<?> closeAccount(String accountNumber);
    
}
