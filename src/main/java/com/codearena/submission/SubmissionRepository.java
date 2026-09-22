package com.codearena.submission; 
import java.util.*; 
import org.springframework.data.jpa.repository.JpaRepository; 

public interface SubmissionRepository extends JpaRepository<Submission,UUID>{ List<Submission> findByStudentIdOrderByCreatedAtDesc(UUID studentId); }
