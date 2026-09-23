package com.codearena.problem;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="test_cases")
public class TestCase {
    @Id @GeneratedValue(strategy=GenerationType.UUID) public UUID id;
    @Column(name="problem_id",nullable=false) public UUID problemId;
    @Column(name="input_data",nullable=false,columnDefinition="text") public String inputData;
    @Column(name="expected_output",nullable=false,columnDefinition="text") public String expectedOutput;
    @Column(nullable=false) public boolean hidden=true;
    @Column(nullable=false) public int weight=1;
    @Column(name="case_kind",nullable=false) public String caseKind="STANDARD";
    @Column(name="created_at",nullable=false) public Instant createdAt=Instant.now();
}
