'use client';

import { Database, ShieldCheck, ArrowRight } from 'lucide-react';
import Link from 'next/link';

export default function LoginPage() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-slate-950 text-slate-100 p-4 font-mono">
      <div className="w-full max-w-sm p-6 rounded-lg border border-slate-800 bg-slate-900 space-y-6 shadow-2xl">
        <div className="text-center space-y-2">
          <div className="inline-flex h-12 w-12 items-center justify-center rounded-lg bg-blue-600 text-white font-bold mb-2 shadow-lg">
            <Database className="h-6 w-6" />
          </div>
          <h2 className="text-lg font-bold tracking-tight text-white">DATAPULSE AI</h2>
          <p className="text-xs text-slate-400">Enterprise Single Sign-On Portal</p>
        </div>

        <div className="space-y-4">
          <div>
            <label className="block text-xs text-slate-400 mb-1">Corporate Email</label>
            <input
              type="email"
              defaultValue="architect@enterprise.internal"
              readOnly
              className="w-full rounded border border-slate-800 bg-slate-950 px-3 py-2 text-xs text-slate-300 focus:outline-none"
            />
          </div>

          <div>
            <label className="block text-xs text-slate-400 mb-1">Password</label>
            <input
              type="password"
              defaultValue="••••••••••••"
              readOnly
              className="w-full rounded border border-slate-800 bg-slate-950 px-3 py-2 text-xs text-slate-300 focus:outline-none"
            />
          </div>

          <Link
            href="/"
            className="w-full flex items-center justify-center gap-2 rounded bg-blue-600 hover:bg-blue-700 text-white py-2 text-xs font-semibold shadow transition-colors"
          >
            Authenticate Workspace <ArrowRight className="h-4 w-4" />
          </Link>
        </div>

        <div className="pt-4 border-t border-slate-800 text-center text-[10px] text-slate-500 flex items-center justify-center gap-1">
          <ShieldCheck className="h-3.5 w-3.5 text-blue-500" /> OAuth2 / SAML Enterprise Ready
        </div>
      </div>
    </div>
  );
}
