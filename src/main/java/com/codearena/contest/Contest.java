package com.codearena.contest;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="contests")
public class Contest {
    @Id @GeneratedValue(strategy=GenerationType.UUID) public UUID id;
    @Column(name="created_by",nullable=false) public UUID createdBy;
    @Column(nullable=false,length=200) public String name;
    @Column(nullable=false,columnDefinition="text") public String description="";
    @Column(name="starts_at",nullable=false) public Instant startsAt;
    @Column(name="ends_at",nullable=false) public Instant endsAt;
    @Column(name="registration_deadline",nullable=false) public Instant registrationDeadline;
    @Column(nullable=false) public String status="DRAFT";
    @Column(name="maximum_participants") public Integer maximumParticipants;
    @Column(nullable=false) public String visibility="PUBLIC";
    @Column(name="access_code_hash") public String accessCodeHash;
    @Column(name="created_at",nullable=false) public Instant createdAt=Instant.now();
    @Column(name="updated_at",nullable=false) public Instant updatedAt=Instant.now();
    @PreUpdate void update(){updatedAt=Instant.now();}
}
