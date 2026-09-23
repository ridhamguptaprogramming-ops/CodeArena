package com.codearena.submission;

import com.codearena.common.api.ApiResponse;
import com.codearena.common.domain.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController @RequestMapping("/internal/v1/worker")
public class WorkerCallbackController {
    private final SubmissionRepository submissions; private final SimpMessagingTemplate events; private final String workerSecret;
    WorkerCallbackController(SubmissionRepository s,SimpMessagingTemplate events,@Value("${app.worker.callback-secret:}") String secret){submissions=s;this.events=events;workerSecret=secret;}
    record Result(@NotNull SubmissionStatus status,@NotNull Verdict verdict,@Min(0) long executionTimeMs,@Min(0) long memoryUsageKb,@Min(0) int score) {}
    @PutMapping("/submissions/{id}") @Transactional
    ApiResponse<String> update(@PathVariable UUID id,@RequestHeader(value="X-Worker-Token",required=false) String token,@RequestBody @Valid Result r){if(workerSecret.isBlank()||token==null||!java.security.MessageDigest.isEqual(workerSecret.getBytes(),token.getBytes()))throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);var s=submissions.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));if(s.status==SubmissionStatus.COMPLETED) return ApiResponse.of("duplicate ignored");if(r.status==SubmissionStatus.CREATED||r.status==SubmissionStatus.QUEUED)throw new ResponseStatusException(HttpStatus.BAD_REQUEST);boolean finalState=r.status==SubmissionStatus.COMPLETED;if(finalState==(r.verdict==Verdict.PENDING))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Status and verdict are inconsistent");if(s.status==SubmissionStatus.COMPLETED) return ApiResponse.of("duplicate ignored");s.status=r.status;s.verdict=r.verdict;s.executionTimeMs=r.executionTimeMs;s.memoryUsageKb=r.memoryUsageKb;s.score=r.score;if(finalState)s.completedAt=Instant.now();submissions.save(s);events.convertAndSendToUser(s.studentId.toString(),"/queue/submissions",new SubmissionController.SubmissionView(s.id,s.problemId,s.contestId,s.language,s.status,s.verdict,s.executionTimeMs,s.memoryUsageKb,s.score,s.submittedAt,s.completedAt));return ApiResponse.of("updated");}
}
