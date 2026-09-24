import { AppShell, ProblemDetail } from "../../components";
export default async function Page({params}:{params:Promise<{slug:string}>}){const {slug}=await params;return <AppShell><ProblemDetail slug={slug}/></AppShell>}
