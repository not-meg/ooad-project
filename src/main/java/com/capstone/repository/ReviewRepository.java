package com.capstone.repository;

import com.capstone.model.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ReviewRepository extends MongoRepository<Review, String> {

    List<Review> findByTeamId(String teamId);

    List<Review> findByFacultyId(String facultyId);

    List<Review> findByPhase(int phase);

    List<Review> findByStatus(String status);

}
