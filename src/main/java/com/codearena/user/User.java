package com.codearena.user;
import com.codearena.common.domain.Role; import jakarta.persistence.*; import java.time.Instant; import java.util.*;
@Entity @Table(name="users") public class User {
 @Id @GeneratedValue(strategy=GenerationType.UUID) public UUID id;
 @Column(nullable=false,unique=true,length=254) public String email;
 @Column(nullable=false) public String passwordHash;
 @Column(nullable=false) public String displayName;
 @ElementCollection(fetch=FetchType.EAGER) @CollectionTable(name="user_roles",joinColumns=@JoinColumn(name="user_id")) @Enumerated(EnumType.STRING) @Column(name="role") public Set<Role> roles=new HashSet<>();
 @Column(nullable=false) public boolean emailVerified=false; @Column(nullable=false) public Instant createdAt=Instant.now();
}
