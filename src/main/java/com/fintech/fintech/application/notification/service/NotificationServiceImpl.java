package com.fintech.fintech.application.notification.service;

import java.nio.charset.StandardCharsets;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.fintech.fintech.application.auth_users.entities.User;
import com.fintech.fintech.application.enums.NotificationType;
import com.fintech.fintech.application.notification.dtos.NotificationDto;
import com.fintech.fintech.application.notification.entities.Notification;
import com.fintech.fintech.application.notification.repositories.NotificationRepo;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService{

    private final NotificationRepo notificationRepo;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    
    @Override
    @Async
    public void sendEmail(NotificationDto notificationDto, User user) {

        try{
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name()
            );
            helper.setTo(notificationDto.getRecipient());
            helper.setSubject(notificationDto.getSubject());

            if(notificationDto.getTemplateName() != null){
                Context context = new Context();
                context.setVariables(notificationDto.getTemplateVariables());
                String htmlContent = templateEngine.process(notificationDto.getTemplateName(), context);
                helper.setText(htmlContent, true);
                
            }else{
                helper.setText(notificationDto.getBody(), true);
            }
            mailSender.send(mimeMessage);
            log.info("Email sent out");

            Notification notificationToSave = Notification.builder()
            .recipient(notificationDto.getRecipient())
            .subject(notificationDto.getSubject())
            .body(notificationDto.getBody())
            .type(NotificationType.EMAIL)
            .user(user)
            .build();

            notificationRepo.save(notificationToSave);

        }catch(MessagingException e){
            log.error(e.getMessage());
        }


    }
    
}
