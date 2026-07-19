package com.fintech.fintech.application.transaction.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fintech.fintech.application.account.entities.Account;
import com.fintech.fintech.application.account.repositories.AccountRepo;
import com.fintech.fintech.application.auth_users.entities.User;
import com.fintech.fintech.application.auth_users.service.Impl.UserService;
import com.fintech.fintech.application.enums.TransactionStatus;
import com.fintech.fintech.application.enums.TransactionType;
import com.fintech.fintech.application.exceptions.BadRequestException;
import com.fintech.fintech.application.exceptions.InsufficientBalanceException;
import com.fintech.fintech.application.exceptions.InvalidTransactionException;
import com.fintech.fintech.application.exceptions.NotFoundExceptions;
import com.fintech.fintech.application.notification.dtos.NotificationDto;
import com.fintech.fintech.application.notification.service.NotificationService;
import com.fintech.fintech.application.res.Response;
import com.fintech.fintech.application.transaction.dtos.TransactionDto;
import com.fintech.fintech.application.transaction.dtos.TransactionRequestDto;
import com.fintech.fintech.application.transaction.entities.Transaction;
import com.fintech.fintech.application.transaction.repositories.TransactionRepo;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService{

    private final TransactionRepo transactionRepo;
    private final AccountRepo accountRepo;
    private final NotificationService notificationService;
    private final UserService userService;
    private final ModelMapper modelMapper;
    
    @Override
    @Transactional
    public Response<?> createTransaction(TransactionRequestDto transactionRequest) {

        Transaction transaction = new Transaction();
        transaction.setTransactionType(transactionRequest.getTransactionType());
        transaction.setAmount(transactionRequest.getAmount());
        transaction.setDescription(transactionRequest.getDescription());

        switch (transactionRequest.getTransactionType()){
            case DEPOSIT -> handleDeposit(transactionRequest, transaction);
            case WITHDRAWAL -> handleWithdrawal(transactionRequest, transaction);
            case TRANSFER -> handleTransfer(transactionRequest, transaction);
            default -> throw new InvalidTransactionException("Invalid transaction type");
        }

        transaction.setStatus(TransactionStatus.SUCCESS);
        Transaction savedTxn = transactionRepo.save(transaction);

        sendTransactionNotification(savedTxn);

        return Response.builder()
        .statusCode(200)
        .message("Transaction successful")
        .build();

    }

    @Override
    @Transactional
    public Response<List<TransactionDto>> getTransactionsForAnAccount(String accountNumber, int page, int size) {

        User user = userService.getCurrentLoggedInUser();

        Account account = accountRepo.findByAccountNumber(accountNumber).orElseThrow(
            ()->new NotFoundExceptions("Account not found")
        );

        if(!account.getUser().getId().equals(user.getId())){
            throw new BadRequestException("Account does not belong to the authenticated user");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());
        Page<Transaction> txns = transactionRepo.findByAccount_AccountNumber(accountNumber, pageable);

        List<TransactionDto> transactionDtos = txns.getContent().stream().map(transaction ->modelMapper.map(transaction, TransactionDto.class))
        .toList();

        return Response.<List<TransactionDto>>builder()
        .statusCode(HttpStatus.OK.value())
        .message("Transactions retrieved")
        .data(transactionDtos)
        .meta(Map.of(
            "currentPage", txns.getNumber(),
            "totalItems", txns.getTotalElements(),
            "totalPages", txns.getTotalPages(),
            "pageSize", txns.getSize()
        ))
        .build();
    }


    private void handleDeposit(TransactionRequestDto request, Transaction transaction){
        Account account = accountRepo.findByAccountNumber(request.getAccountNumber()).orElseThrow(()-> new NotFoundExceptions("Account not fount"));

        account.setBalance(account.getBalance().add(request.getAmount()));
        transaction.setAccount(account);
        accountRepo.save(account);
    }

    private void handleWithdrawal(TransactionRequestDto request, Transaction transaction){

        Account account = accountRepo.findByAccountNumber(request.getAccountNumber()).orElseThrow(
            ()->new NotFoundExceptions("Account not found")
        );

        if(account.getBalance().compareTo(request.getAmount())<0){
            throw new InsufficientBalanceException("Insufficient balance");
        }

        account.setBalance(account.getBalance().subtract(request.getAmount()));
        transaction.setAccount(account);
        accountRepo.save(account);
    }

    private void handleTransfer(TransactionRequestDto request, Transaction transaction){
        Account sourceAccount = accountRepo.findByAccountNumber(request.getAccountNumber()).orElseThrow(
            ()->new NotFoundExceptions("Account not found")
        );

        Account destination = accountRepo.findByAccountNumber(request.getDestinationAccountNumber()).orElseThrow(
            ()-> new NotFoundExceptions("Destination Account not found")
        );

        if(sourceAccount.getBalance().compareTo(request.getAmount())<0){
            throw new InsufficientBalanceException("Insufficient balance in source account");
        }


        sourceAccount.setBalance(sourceAccount.getBalance().subtract(request.getAmount()));
        accountRepo.save(sourceAccount);

        destination.setBalance(destination.getBalance().add(request.getAmount()));
        accountRepo.save(destination);

        transaction.setAccount(sourceAccount);
        transaction.setSourceAccount(sourceAccount.getAccountNumber());
        transaction.setDestinationAccount(destination.getAccountNumber());
    }

    private void sendTransactionNotification(Transaction tnx){
        User user = tnx.getAccount().getUser();
        String subject;
        String template;

        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("name", user.getFirstName());
        templateVariables.put("amount", tnx.getAmount());
        templateVariables.put("accountNumber", tnx.getAccount().getAccountNumber());
        templateVariables.put("date", tnx.getTransactionDate());
        templateVariables.put("balance", tnx.getAccount().getBalance());

        if(tnx.getTransactionType() == TransactionType.DEPOSIT){
            subject = "Credit Alert";
            template = "credit-alert";

            NotificationDto notificatioEmailToSendOut = NotificationDto.builder()
            .recipient(user.getEmail())
            .subject(subject)
            .templateName(template)
            .templateVariables(templateVariables)
            .build();

            notificationService.sendEmail(notificatioEmailToSendOut, user);
        } else if(
            tnx.getTransactionType() == TransactionType.WITHDRAWAL){
                subject = "Debit Alert";
                template = "debit-alert";

            NotificationDto notificatioEmailToSendOut = NotificationDto.builder()
            .recipient(user.getEmail())
            .subject(subject)
            .templateName(template)
            .templateVariables(templateVariables)
            .build();

            notificationService.sendEmail(notificatioEmailToSendOut, user);
            } else if(tnx.getTransactionType() == TransactionType.TRANSFER){

                subject = "Debit Alert";
                template = "debit-alert";

        NotificationDto notificatioEmailToSendOut = NotificationDto.builder()
            .recipient(user.getEmail())
            .subject(subject)
            .templateName(template)
            .templateVariables(templateVariables)
            .build();
            notificationService.sendEmail(notificatioEmailToSendOut, user); 


            Account destination = accountRepo.findByAccountNumber(tnx.getDestinationAccount()).orElseThrow(
                ()-> new NotFoundExceptions("Destination account not found")
            );

            User reciever = destination.getUser();


            Map<String, Object> recvVars = new HashMap<>();
            recvVars.put("name", reciever.getFirstName());
            recvVars.put("amount", tnx.getAmount());
            recvVars.put("accountNumber", destination.getAccountNumber());
            recvVars.put("date", tnx.getTransactionDate());
            recvVars.put("balance", destination.getBalance());

            NotificationDto notificatioEmailToSendOutToReciever = NotificationDto.builder()
            .recipient(reciever.getEmail())
            .subject("Credit Alert")
            .templateName("credit-alert")
            .templateVariables(recvVars)
            .build();

            notificationService.sendEmail(notificatioEmailToSendOutToReciever, user); 

            

            }
        
    }

}
