package com.fintech.fintech.application.notification.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fintech.fintech.application.notification.entities.Notification;

public interface NotificationRepo extends JpaRepository<Notification, Long>{
    
}
