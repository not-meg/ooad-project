package com.capstone.repository;

import com.capstone.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationsRepository extends MongoRepository<Notification, String> {
    List<Notification> findByTeamId(String teamId);
    List<Notification> findByFacultyId(String facultyId);
    List<Notification> findByPanelMemberIdsContaining(String panelMemberId);
    Optional<Notification> findByNotificationId(String notificationId);
    List<Notification> findByExpireAtBefore(String expireAt);
    List<Notification> findByReviewDate(String reviewDate);
}
