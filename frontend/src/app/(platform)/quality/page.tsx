'use client';

import { FileCheck } from 'lucide-react';

export default function QualityPage() {
  return (
    <div className="space-y-6 font-mono">
      <div className="border-b border-slate-200 dark:border-slate-800 pb-4">
        <h1 className="text-xl font-bold tracking-tight text-slate-900 dark:text-slate-100 flex items-center gap-2">
          <FileCheck className="h-5 w-5 text-emerald-500" /> DATA QUALITY RULES CATALOG
        </h1>
        <p className="text-xs text-slate-500 mt-0.5">
          Enterprise rule suite enforcement and constraint policies across database schemas.
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="p-4 rounded-lg border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-950 space-y-2">
          <div className="text-xs text-slate-500 uppercase">Total Active Rules</div>
          <div className="text-2xl font-bold text-slate-900 dark:text-slate-100">42 Rules</div>
          <div className="text-[11px] text-emerald-600">All rules active</div>
        </div>

        <div className="p-4 rounded-lg border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-950 space-y-2">
          <div className="text-xs text-slate-500 uppercase">Average Compliance</div>
          <div className="text-2xl font-bold text-emerald-600">99.4%</div>
          <div className="text-[11px] text-slate-400">Target: &gt; 98%</div>
        </div>

        <div className="p-4 rounded-lg border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-950 space-y-2">
          <div className="text-xs text-slate-500 uppercase">Schema Violations</div>
          <div className="text-2xl font-bold text-slate-900 dark:text-slate-100">0 Violations</div>
          <div className="text-[11px] text-emerald-600">Zero critical failures</div>
        </div>
      </div>
    </div>
  );
}
