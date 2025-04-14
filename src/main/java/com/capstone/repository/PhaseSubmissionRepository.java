package com.capstone.repository;

import com.capstone.model.PhaseSubmission;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PhaseSubmissionRepository extends MongoRepository<PhaseSubmission, String> {
    List<PhaseSubmission> findByTeamID(String teamID);
}
