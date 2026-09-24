import { AppShell, Workspace } from "../../components";
export default async function Page({params}:{params:Promise<{problemId:string}>}){const {problemId}=await params;return <AppShell><Workspace problemId={problemId}/></AppShell>}
