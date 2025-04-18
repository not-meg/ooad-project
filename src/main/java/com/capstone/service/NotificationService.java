package com.capstone.service;

import com.capstone.model.Notification;
import com.capstone.repository.NotificationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationsRepository notificationsRepository;

    public List<Notification> getNotificationsByTeamId(String teamId) {
        return notificationsRepository.findByTeamId(teamId);
    }
}
