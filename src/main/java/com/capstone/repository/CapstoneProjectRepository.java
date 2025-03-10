package com.capstone.repository;

import com.capstone.model.CapstoneProject;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CapstoneProjectRepository extends MongoRepository<CapstoneProject, String> {
}
