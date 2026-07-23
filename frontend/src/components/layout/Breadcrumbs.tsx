'use client';

import { ChevronRight, Home } from 'lucide-react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';

export function Breadcrumbs() {
  const pathname = usePathname();
  const segments = pathname.split('/').filter(Boolean);

  return (
    <nav className="flex items-center gap-1.5 text-xs text-slate-500 font-mono py-2 px-1">
      <Link href="/" className="flex items-center gap-1 hover:text-slate-900 dark:hover:text-slate-100 transition-colors">
        <Home className="h-3.5 w-3.5" />
        <span>Root</span>
      </Link>
      {segments.map((segment, index) => {
        const href = `/${segments.slice(0, index + 1).join('/')}`;
        const isLast = index === segments.length - 1;
        const formatted = segment.charAt(0).toUpperCase() + segment.slice(1);

        return (
          <div key={href} className="flex items-center gap-1.5">
            <ChevronRight className="h-3 w-3 text-slate-400" />
            {isLast ? (
              <span className="font-semibold text-slate-900 dark:text-slate-100">{formatted}</span>
            ) : (
              <Link href={href} className="hover:text-slate-900 dark:hover:text-slate-100 transition-colors">
                {formatted}
              </Link>
            )}
          </div>
        );
      })}
    </nav>
  );
}
