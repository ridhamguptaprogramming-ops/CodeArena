import type { ApiEnvelope, Contest, Problem, Submission } from "./types";

const base = (process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080").replace(/\/$/, "");
let accessToken: string | null = null;
export function setAccessToken(token: string | null) { accessToken = token; }

async function request<T>(path: string, init: RequestInit = {}): Promise<T> {
  const headers = new Headers(init.headers);
  headers.set("Accept", "application/json");
  if (init.body) headers.set("Content-Type", "application/json");
  if (accessToken) headers.set("Authorization", `Bearer ${accessToken}`);
  const response = await fetch(`${base}${path}`, { ...init, headers, cache: "no-store" });
  const body = await response.json().catch(() => null) as ApiEnvelope<T> | { message?: string } | null;
  if (!response.ok) throw new Error((body && "message" in body && body.message) || `Request failed (${response.status})`);
  return body && "data" in body ? body.data : body as T;
}

export const authApi = {
  login: (email: string, password: string) => request<{ accessToken: string; refreshToken: string }>("/api/v1/auth/login", { method: "POST", body: JSON.stringify({ email, password }) }),
  register: (displayName: string, email: string, password: string) => request<{ accessToken: string; refreshToken: string }>("/api/v1/auth/register", { method: "POST", body: JSON.stringify({ displayName, email, password }) }),
};
export const problemsApi = {
  list: () => request<Problem[]>("/api/v1/problems"),
  byId: (id: string) => request<Problem>(`/api/v1/problems/${encodeURIComponent(id)}`),
};
export const contestsApi = {
  list: (page = 0, size = 20) => request<{ content: Contest[]; totalElements: number }>(`/api/v1/contests?page=${page}&size=${size}`),
  register: (id: string) => request<{ status: string }>(`/api/v1/contests/${encodeURIComponent(id)}/register`, { method: "POST", body: "{}" }),
};
export const submissionsApi = {
  list: (page = 0, size = 20) => request<{ content: Submission[]; totalElements: number }>(`/api/v1/submissions?page=${page}&size=${size}`),
  get: (id: string) => request<Submission>(`/api/v1/submissions/${encodeURIComponent(id)}`),
  create: (input: { problemId: string; contestId?: string; language: string; sourceCode: string }) => request<Submission>("/api/v1/submissions", { method: "POST", body: JSON.stringify(input) }),
};
