package com.fintech.fintech.application.notification.service;

import com.fintech.fintech.application.auth_users.entities.User;
import com.fintech.fintech.application.notification.dtos.NotificationDto;

public interface NotificationService {

    void sendEmail(NotificationDto notificationDto, User user);
}
