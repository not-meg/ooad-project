package com.capstone.repository;

import com.capstone.model.Notification; 
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface NotificationsRepository extends MongoRepository<Notification, String> {
    
    List<Notification> findByTeamId(String teamId);

}
