package com.codearena.user;
import com.codearena.common.domain.Role; import jakarta.persistence.*; import java.time.Instant; import java.util.*;
@Entity @Table(name="users") public class User {
 @Id @GeneratedValue(strategy=GenerationType.UUID) public UUID id;
 @Column(nullable=false,unique=true,length=254) public String email;
 @Column(name="password_hash",nullable=false) public String passwordHash;
 @Column(name="display_name",nullable=false) public String displayName;
 public String college; public String department; @Column(name="profile_image_url",columnDefinition="text") public String profileImageUrl;
 @ElementCollection(fetch=FetchType.EAGER) @CollectionTable(name="user_roles",joinColumns=@JoinColumn(name="user_id")) @Enumerated(EnumType.STRING) @Column(name="role") public Set<Role> roles=new HashSet<>();
 @Column(name="email_verified",nullable=false) public boolean emailVerified=false; @Column(name="account_status",nullable=false) public String accountStatus="ACTIVE";
 @Column(name="created_at",nullable=false) public Instant createdAt=Instant.now(); @Column(name="updated_at",nullable=false) public Instant updatedAt=Instant.now();
 @PreUpdate void updated(){updatedAt=Instant.now();}
}
