package com.codearena.problem;
import jakarta.persistence.*;
import java.util.UUID;
@Entity @Table(name="tags") public class Tag { @Id @GeneratedValue(strategy=GenerationType.UUID) public UUID id; @Column(nullable=false,unique=true,length=80) public String name; @Column(name="slug",nullable=false,unique=true,length=90) public String slug; }
