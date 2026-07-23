'use client';

import { Settings, Server, Moon, Sun } from 'lucide-react';
import { useState } from 'react';

export function SettingsView() {
  const [theme, setTheme] = useState<'dark' | 'light'>('dark');

  const toggleTheme = () => {
    const next = theme === 'dark' ? 'light' : 'dark';
    setTheme(next);
    if (next === 'dark') {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
  };

  return (
    <div className="space-y-6 font-mono max-w-4xl">
      <div className="border-b border-slate-200 dark:border-slate-800 pb-4">
        <h1 className="text-xl font-bold tracking-tight text-slate-900 dark:text-slate-100 flex items-center gap-2">
          <Settings className="h-5 w-5 text-blue-500" /> PLATFORM CONFIGURATION & ENVIRONMENT
        </h1>
        <p className="text-xs text-slate-500 mt-0.5">
          DataPulse AI platform preferences, API base URL settings, and system environment info.
        </p>
      </div>

      <div className="space-y-6">
        {/* Theme Settings */}
        <div className="p-5 rounded-lg border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-950 space-y-3">
          <h3 className="text-xs font-bold text-slate-700 dark:text-slate-300 uppercase flex items-center gap-2">
            {theme === 'dark' ? <Moon className="h-4 w-4 text-blue-400" /> : <Sun className="h-4 w-4 text-amber-500" />} Appearance Theme
          </h3>
          <p className="text-xs text-slate-500 font-sans">
            Toggle interface theme between Enterprise Dark Mode and Light Mode.
          </p>
          <button
            onClick={toggleTheme}
            className="px-4 py-2 text-xs font-semibold rounded bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 dark:hover:bg-slate-700 text-slate-900 dark:text-slate-100 transition-colors"
          >
            Switch to {theme === 'dark' ? 'Light Mode' : 'Dark Mode'}
          </button>
        </div>

        {/* API Endpoint Settings */}
        <div className="p-5 rounded-lg border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-950 space-y-3">
          <h3 className="text-xs font-bold text-slate-700 dark:text-slate-300 uppercase flex items-center gap-2">
            <Server className="h-4 w-4 text-emerald-500" /> Backend API Environment
          </h3>
          <div className="space-y-2 text-xs">
            <div className="flex justify-between border-b pb-2">
              <span className="text-slate-500">API Base URL:</span>
              <span className="font-bold text-blue-600">http://localhost:8081/api/v1</span>
            </div>
            <div className="flex justify-between border-b pb-2">
              <span className="text-slate-500">MinIO Storage:</span>
              <span className="text-slate-900 dark:text-slate-100">http://localhost:9000</span>
            </div>
            <div className="flex justify-between border-b pb-2">
              <span className="text-slate-500">Kafka Bootstrap:</span>
              <span className="text-slate-900 dark:text-slate-100">localhost:9092</span>
            </div>
            <div className="flex justify-between">
              <span className="text-slate-500">App Version:</span>
              <span className="text-slate-900 dark:text-slate-100">v1.0.0-RELEASE</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
