package com.fintech.fintech.application.notification.dtos;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fintech.fintech.application.enums.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {

    private Long id;

    private String subject;

    private String recipient;

    private String body;

    private NotificationType type;

    private LocalDateTime createdAt;

    private String templateName;
    private Map<String, Object> templateVariables;


    
}
