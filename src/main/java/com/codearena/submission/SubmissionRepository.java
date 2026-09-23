package com.codearena.submission; 
import java.util.*; 
import org.springframework.data.jpa.repository.JpaRepository; import org.springframework.data.domain.*; 

public interface SubmissionRepository extends JpaRepository<Submission,UUID>{ Page<Submission> findByStudentId(UUID studentId, Pageable pageable); }
