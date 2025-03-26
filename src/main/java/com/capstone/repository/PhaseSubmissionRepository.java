package com.capstone.repository;

import com.capstone.model.PhaseSubmission;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface PhaseSubmissionRepository extends MongoRepository<PhaseSubmission, String> {
    List<PhaseSubmission> findByTeamID(String teamID);
}
