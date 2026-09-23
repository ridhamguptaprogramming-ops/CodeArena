package com.codearena.problem;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestCaseRepository extends JpaRepository<TestCase, UUID> {
    List<TestCase> findByProblemIdAndHiddenFalseOrderByCreatedAtAsc(UUID problemId);
}
