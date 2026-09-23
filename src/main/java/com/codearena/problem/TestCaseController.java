package com.codearena.problem;

import com.codearena.common.api.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController @RequestMapping("/api/v1/problems/{problemId}/test-cases")
public class TestCaseController {
    private final TestCaseRepository cases;
    private final ProblemRepository problems;
    TestCaseController(TestCaseRepository cases, ProblemRepository problems) { this.cases=cases; this.problems=problems; }
    record PublicCase(UUID id, String input, String expectedOutput) {}
    record Create(@NotBlank String input, @NotBlank String expectedOutput, boolean hidden, @Min(1) @Max(1000) int weight, String kind) {}
    @GetMapping
    public ApiResponse<List<PublicCase>> visible(@PathVariable UUID problemId) {
        if (!problems.existsByIdAndPublishedTrue(problemId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return ApiResponse.of(cases.findByProblemIdAndHiddenFalseOrderByCreatedAtAsc(problemId).stream().map(c -> new PublicCase(c.id,c.inputData,c.expectedOutput)).toList());
    }
    @PostMapping @PreAuthorize("hasAnyRole('TEACHER','ADMIN','SUPER_ADMIN')") @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Map<String,Object>> create(@PathVariable UUID problemId, @RequestBody @Valid Create body) {
        var p=problems.findById(problemId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        var c=new TestCase(); c.problemId=p.id; c.inputData=body.input; c.expectedOutput=body.expectedOutput; c.hidden=body.hidden; c.weight=body.weight; c.caseKind=body.kind==null?"STANDARD":body.kind.toUpperCase(Locale.ROOT);
        c=cases.save(c); return ApiResponse.of(Map.of("id",c.id,"hidden",c.hidden,"kind",c.caseKind));
    }
}
