package com.codearena.problem;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface TagRepository extends JpaRepository<Tag,UUID> { Optional<Tag> findByNameIgnoreCase(String name); }
