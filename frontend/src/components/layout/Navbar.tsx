'use client';

import { Bell, Database, Search, ShieldCheck, User } from 'lucide-react';
import Link from 'next/link';

export function Navbar() {
  return (
    <header className="sticky top-0 z-30 flex h-14 w-full items-center justify-between border-b border-slate-200 dark:border-slate-800 bg-white/95 dark:bg-slate-950/95 px-4 backdrop-blur">
      <div className="flex items-center gap-4">
        <Link href="/" className="flex items-center gap-2.5">
          <div className="flex h-8 w-8 items-center justify-center rounded-md bg-blue-600 text-white font-bold shadow-sm">
            <Database className="h-4 w-4" />
          </div>
          <div className="flex flex-col">
            <span className="text-sm font-bold tracking-tight text-slate-900 dark:text-slate-100">
              DATAPULSE <span className="text-blue-600 dark:text-blue-400">AI</span>
            </span>
            <span className="text-[10px] font-mono text-slate-500 uppercase tracking-widest leading-none">
              Enterprise Data Engine
            </span>
          </div>
        </Link>

        <div className="hidden md:flex items-center gap-2 ml-6 pl-6 border-l border-slate-200 dark:border-slate-800">
          <span className="px-2 py-0.5 text-[10px] font-mono font-semibold bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 border border-emerald-500/20 rounded">
            PROD WORKSPACE
          </span>
          <span className="flex items-center gap-1 text-xs text-slate-500 font-mono">
            <ShieldCheck className="h-3.5 w-3.5 text-blue-500" /> API v1.0.0
          </span>
        </div>
      </div>

      <div className="flex items-center gap-3">
        {/* Global Search Bar */}
        <div className="relative hidden sm:block w-64">
          <Search className="absolute left-2.5 top-2.5 h-3.5 w-3.5 text-slate-400" />
          <input
            type="text"
            placeholder="Search datasets, columns, rules..."
            className="h-8 w-full rounded-md border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-900 pl-8 pr-3 text-xs text-slate-900 dark:text-slate-100 placeholder:text-slate-400 focus:outline-none focus:ring-1 focus:ring-blue-500"
          />
        </div>

        <button className="relative flex h-8 w-8 items-center justify-center rounded-md border border-slate-200 dark:border-slate-800 text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-900">
          <Bell className="h-4 w-4" />
          <span className="absolute top-1.5 right-1.5 h-1.5 w-1.5 rounded-full bg-blue-600" />
        </button>

        <div className="flex items-center gap-2 pl-2 border-l border-slate-200 dark:border-slate-800">
          <div className="flex h-7 w-7 items-center justify-center rounded-full bg-slate-200 dark:bg-slate-800 text-slate-700 dark:text-slate-300 font-mono text-xs font-semibold">
            <User className="h-3.5 w-3.5" />
          </div>
          <span className="hidden lg:inline text-xs font-medium text-slate-700 dark:text-slate-300">
            Data Architect
          </span>
        </div>
      </div>
    </header>
  );
}
