package com.codearena.contest;

import com.codearena.common.api.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController @RequestMapping("/api/v1/contests")
public class ContestController {
    private final ContestRepository contests; private final ContestParticipantRepository participants;
    ContestController(ContestRepository c,ContestParticipantRepository p){contests=c;participants=p;}
    record Create(@NotBlank @Size(max=200) String name,String description,@NotNull Instant startsAt,@NotNull Instant endsAt,@NotNull Instant registrationDeadline,@Min(1) Integer maximumParticipants,@Pattern(regexp="PUBLIC|PRIVATE") String visibility,String accessCode) {}
    record ContestCard(UUID id,String name,String description,Instant startsAt,Instant endsAt,Instant registrationDeadline,String status,Integer maximumParticipants,String visibility) { static ContestCard from(Contest c){return new ContestCard(c.id,c.name,c.description,c.startsAt,c.endsAt,c.registrationDeadline,c.status,c.maximumParticipants,c.visibility);} }
    @GetMapping ApiResponse<Page<ContestCard>> browse(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){return ApiResponse.of(contests.findByVisibilityAndStatusIn("PUBLIC",List.of("SCHEDULED","ACTIVE","ENDED","RESULT_PUBLISHED"),PageRequest.of(Math.max(0,page),Math.min(100,Math.max(1,size)),Sort.by("startsAt").descending())).map(ContestCard::from));}
    @PostMapping @PreAuthorize("hasAnyRole('TEACHER','ADMIN','SUPER_ADMIN')") @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<ContestCard> create(@AuthenticationPrincipal String uid,@RequestBody @Valid Create c){if(!c.endsAt.isAfter(c.startsAt)||c.registrationDeadline.isAfter(c.startsAt))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Contest dates are inconsistent");var x=new Contest();x.createdBy=UUID.fromString(uid);x.name=c.name;x.description=c.description==null?"":c.description;x.startsAt=c.startsAt;x.endsAt=c.endsAt;x.registrationDeadline=c.registrationDeadline;x.maximumParticipants=c.maximumParticipants;x.visibility=c.visibility==null?"PUBLIC":c.visibility;x.status="SCHEDULED";return ApiResponse.of(ContestCard.from(contests.save(x)));}
    @PostMapping("/{id}/register") @PreAuthorize("hasRole('STUDENT')") @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<Map<String,String>> register(@AuthenticationPrincipal String uid,@PathVariable UUID id,@RequestBody(required=false) Map<String,String> body){var c=contests.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));var now=Instant.now();if(!c.visibility.equals("PUBLIC")||!c.status.equals("SCHEDULED")||now.isAfter(c.registrationDeadline))throw new ResponseStatusException(HttpStatus.CONFLICT,"Contest registration is closed");if(c.maximumParticipants!=null&&participants.countByContestId(id)>=c.maximumParticipants)throw new ResponseStatusException(HttpStatus.CONFLICT,"Contest is full");var p=new ContestParticipant();p.contestId=id;p.studentId=UUID.fromString(uid);try{participants.saveAndFlush(p);}catch(org.springframework.dao.DataIntegrityViolationException duplicate){throw new ResponseStatusException(HttpStatus.CONFLICT,"Already registered");}return ApiResponse.of(Map.of("status","REGISTERED"));}
}
