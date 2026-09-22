import type { Metadata } from "next";
import "./globals.css";
export const metadata: Metadata = { title: "CodeArena — Code Beyond Limits", description: "Secure, intelligent coding assessments." };
export default function Layout({children}:{children:React.ReactNode}) { return <html lang="en"><body>{children}</body></html>; }
