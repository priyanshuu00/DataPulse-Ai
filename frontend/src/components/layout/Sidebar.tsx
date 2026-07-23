'use client';

import {
  Activity,
  Database,
  FileCheck,
  LayoutDashboard,
  Settings,
  Sparkles,
  UploadCloud,
} from 'lucide-react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { cn } from '../../lib/utils';

const navItems = [
  { name: 'Dashboard', href: '/', icon: LayoutDashboard },
  { name: 'Datasets', href: '/datasets', icon: Database },
  { name: 'Pipeline Monitor', href: '/pipeline', icon: Activity },
  { name: 'AI Recommendations', href: '/recommendations', icon: Sparkles },
  { name: 'Quality Rules', href: '/quality', icon: FileCheck },
  { name: 'Settings', href: '/settings', icon: Settings },
];

export function Sidebar({ onOpenUpload }: { onOpenUpload?: () => void }) {
  const pathname = usePathname();

  return (
    <aside className="w-56 shrink-0 border-r border-slate-200 dark:border-slate-800 bg-slate-50/50 dark:bg-slate-950/50 min-h-[calc(100vh-3.5rem)] flex flex-col justify-between p-3">
      <div className="space-y-4">
        {/* Primary Action Button */}
        {onOpenUpload && (
          <button
            onClick={onOpenUpload}
            className="w-full flex items-center justify-center gap-2 rounded-md bg-blue-600 hover:bg-blue-700 text-white px-3 py-2 text-xs font-semibold shadow-sm transition-colors"
          >
            <UploadCloud className="h-4 w-4" />
            Ingest Dataset
          </button>
        )}

        {/* Navigation Items */}
        <nav className="space-y-1">
          {navItems.map((item) => {
            const isActive = pathname === item.href || (item.href !== '/' && pathname.startsWith(item.href));
            const Icon = item.icon;
            return (
              <Link
                key={item.name}
                href={item.href}
                className={cn(
                  'flex items-center gap-2.5 rounded-md px-3 py-2 text-xs font-medium transition-colors',
                  isActive
                    ? 'bg-blue-50 dark:bg-blue-950/60 text-blue-600 dark:text-blue-400 font-semibold border-r-2 border-blue-600'
                    : 'text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-900 hover:text-slate-900 dark:hover:text-slate-100'
                )}
              >
                <Icon className={cn('h-4 w-4', isActive ? 'text-blue-600 dark:text-blue-400' : 'text-slate-400')} />
                {item.name}
              </Link>
            );
          })}
        </nav>
      </div>

      {/* Footer System Status */}
      <div className="rounded-md border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900 p-2.5 space-y-1 text-[11px]">
        <div className="flex items-center justify-between text-slate-500 font-mono">
          <span>Engine Status</span>
          <span className="flex items-center gap-1 text-emerald-600 dark:text-emerald-400 font-semibold">
            <span className="h-1.5 w-1.5 rounded-full bg-emerald-500 animate-ping" />
            ONLINE
          </span>
        </div>
        <div className="flex items-center justify-between text-slate-400 font-mono text-[10px]">
          <span>Cluster: Spark-01</span>
          <span>Kafka: Up</span>
        </div>
      </div>
    </aside>
  );
}
