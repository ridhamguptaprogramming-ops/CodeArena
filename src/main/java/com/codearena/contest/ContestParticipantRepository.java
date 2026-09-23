package com.codearena.contest;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ContestParticipantRepository extends JpaRepository<ContestParticipant,ContestParticipantKey> {
 long countByContestId(UUID contestId);
}
