package com.codearena.security;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="refresh_tokens")
public class RefreshToken {
    @Id @GeneratedValue(strategy=GenerationType.UUID) public UUID id;
    @Column(name="user_id",nullable=false) public UUID userId;
    @Column(name="token_hash",nullable=false,unique=true,length=128) public String tokenHash;
    @Column(name="expires_at",nullable=false) public Instant expiresAt;
    @Column(name="revoked_at") public Instant revokedAt;
    @Column(name="created_at",nullable=false) public Instant createdAt=Instant.now();
}
