package com.codearena.contest;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ContestRepository extends JpaRepository<Contest,UUID> {
 Page<Contest> findByVisibilityAndStatusIn(String visibility, Collection<String> statuses, Pageable pageable);
}
