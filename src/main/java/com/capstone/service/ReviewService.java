package com.capstone.service;

import com.capstone.model.Review;
import com.capstone.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public Optional<Review> getReviewById(String id) {
        return reviewRepository.findById(id);
    }

    public List<Review> getReviewsByTeamId(String teamId) {
        return reviewRepository.findByTeamId(teamId);
    }

    public List<Review> getReviewsByFacultyId(String facultyId) {
        return reviewRepository.findByFacultyId(facultyId);
    }

    public List<Review> getReviewsByPhase(int phase) {
        return reviewRepository.findByPhase(phase);
    }

    public List<Review> getReviewsByStatus(String status) {
        return reviewRepository.findByStatus(status);
    }

    public Review saveReview(Review review) {
        return reviewRepository.save(review);
    }

    public void deleteReview(String id) {
        reviewRepository.deleteById(id);
    }

    public Review updateReview(String id, Review updatedReview) {
        Optional<Review> existingReviewOptional = reviewRepository.findById(id);
        return existingReviewOptional.map(existingReview -> {
            updatedReview.setId(id); // Ensure the ID is the same
            return reviewRepository.save(updatedReview);
        }).orElse(null); // Or throw an exception indicating the review was not found
    }
}