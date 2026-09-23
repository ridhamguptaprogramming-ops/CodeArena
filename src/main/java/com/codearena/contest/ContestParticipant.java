package com.codearena.contest;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="contest_participants") @IdClass(ContestParticipantKey.class)
public class ContestParticipant {
    @Id @Column(name="contest_id",nullable=false) public UUID contestId;
    @Id @Column(name="student_id",nullable=false) public UUID studentId;
    @Column(name="registered_at",nullable=false) public Instant registeredAt=Instant.now();
    @Column(name="participation_status",nullable=false) public String participationStatus="REGISTERED";
    @Column(name="final_score",nullable=false) public int finalScore=0;
    @Column(name="final_rank") public Integer finalRank;
}
