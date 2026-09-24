export type ApiEnvelope<T> = { data: T };
export type Problem = {
  id: string; title: string; slug: string; description: string; difficulty: "EASY" | "MEDIUM" | "HARD";
  timeLimitMs: number; memoryLimitMb: number; maximumScore: number; supportedLanguages: string[]; tags: string[];
};
export type Contest = {
  id: string; name: string; description: string; startsAt: string; endsAt: string; registrationDeadline: string;
  status: string; maximumParticipants: number | null; visibility: string;
};
export type Submission = {
  id: string; problemId: string; contestId: string | null; language: string; status: string; verdict: string;
  executionTimeMs: number | null; memoryUsageKb: number | null; score: number | null; submittedAt: string; completedAt: string | null;
};
