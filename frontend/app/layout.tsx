import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "CodeArena — Code Beyond Limits",
  description: "A secure, scalable coding assessment platform for students and educators.",
};

export default function Layout({ children }: Readonly<{ children: React.ReactNode }>) {
  return <html lang="en"><body>{children}</body></html>;
}
