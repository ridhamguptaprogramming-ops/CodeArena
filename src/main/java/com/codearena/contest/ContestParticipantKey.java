package com.codearena.contest;

import java.io.Serializable;
import java.util.UUID;

public class ContestParticipantKey implements Serializable {
    public UUID contestId;
    public UUID studentId;
    public ContestParticipantKey() {}
    public ContestParticipantKey(UUID contestId, UUID studentId) { this.contestId=contestId; this.studentId=studentId; }
    @Override public boolean equals(Object value) { return value instanceof ContestParticipantKey key && contestId.equals(key.contestId) && studentId.equals(key.studentId); }
    @Override public int hashCode() { return java.util.Objects.hash(contestId,studentId); }
}
