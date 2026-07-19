package com.fintech.fintech.application.account.service;

import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.modelmapper.ModelMapper;
import org.springframework.boot.webmvc.autoconfigure.WebMvcProperties.Apiversion.Use;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fintech.fintech.application.account.dtos.AccountDtos;
import com.fintech.fintech.application.account.entities.Account;
import com.fintech.fintech.application.account.repositories.AccountRepo;
import com.fintech.fintech.application.auth_users.entities.User;
import com.fintech.fintech.application.auth_users.service.Impl.UserService;
import com.fintech.fintech.application.enums.AccountStatus;
import com.fintech.fintech.application.enums.AccountType;
import com.fintech.fintech.application.enums.Currency;
import com.fintech.fintech.application.exceptions.BadRequestException;
import com.fintech.fintech.application.exceptions.NotFoundExceptions;
import com.fintech.fintech.application.res.Response;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepo accountRepo;
    private final UserService userService;
    private final ModelMapper modelMapper;

    private final Random random = new Random();
    
    @Override
    public Account createAccount(AccountType accountType, User user) {
        log.info(null);


        String accountNumber = generateAccountNumber();

        Account account = Account.builder()
        .accountNumber(accountNumber)
        .accountType(accountType)
        .currency(Currency.USD)
        .balance(BigDecimal.ZERO)
        .status(AccountStatus.ACTIVE)
        .user(user)
        .createdAt(LocalDateTime.now())
        .build();
    
    return accountRepo.save(account);

    }

    @Override
    public Response<List<AccountDtos>> getMyAccounts() {

        User user = userService.getCurrentLoggedInUser();
        List<AccountDtos> accounts = accountRepo.findByUserId(user.getId())
        .stream()
        .map(account -> modelMapper.map(account, AccountDtos.class))
        .toList();

        return Response.<List<AccountDtos>>builder()
        .statusCode(HttpStatus.OK.value())
        .message("User accounts fetched successfully")
        .data(accounts)
        .build();
    }

    @Override
    public Response<?> closeAccount(String accountNumber) {

        User user = userService.getCurrentLoggedInUser();
        Account account = accountRepo.findByAccountNumber(accountNumber).orElseThrow(()->new NotFoundExceptions("Account Not Found"));

        if(!user.getAccounts().contains(account)){
            throw new NotFoundExceptions("Account doesn't belong to you");
        }
        if(account.getBalance().compareTo(BigDecimal.ZERO)>0){
            throw new BadRequestException("Account balance must be zero before closing");
        }
        account.setStatus(AccountStatus.CLOSED);
        account.setClosedAt(LocalDateTime.now());
        accountRepo.save(account);


        return Response.builder()
        .statusCode(HttpStatus.OK.value())
        .message("Account closed successfully.")
        .build();

    }


    private String generateAccountNumber(){
        String accountNumber;
        do{
            accountNumber = "66" + (random.nextInt(90000000)+ 10000000);

        }while(accountRepo.findByAccountNumber(accountNumber).isPresent());

        log.info("account number generated {}", accountRepo);

        return accountNumber;
    }
    
}
