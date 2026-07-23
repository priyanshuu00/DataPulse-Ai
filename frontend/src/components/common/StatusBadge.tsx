import { VersionStatus } from '../../types/dataset';
import { cn } from '../../lib/utils';

interface StatusBadgeProps {
  status: VersionStatus | string;
  className?: string;
}

export function StatusBadge({ status, className }: StatusBadgeProps) {
  const normalized = status?.toUpperCase() || 'UPLOADED';

  const getStatusStyles = (st: string) => {
    switch (st) {
      case 'READY':
        return 'bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 border-emerald-500/20';
      case 'PROCESSING':
        return 'bg-blue-500/10 text-blue-600 dark:text-blue-400 border-blue-500/20 animate-pulse';
      case 'VALIDATING':
        return 'bg-amber-500/10 text-amber-600 dark:text-amber-400 border-amber-500/20';
      case 'FAILED':
        return 'bg-rose-500/10 text-rose-600 dark:text-rose-400 border-rose-500/20';
      case 'UPLOADING':
        return 'bg-purple-500/10 text-purple-600 dark:text-purple-400 border-purple-500/20';
      case 'UPLOADED':
      default:
        return 'bg-slate-500/10 text-slate-600 dark:text-slate-400 border-slate-500/20';
    }
  };

  return (
    <span
      className={cn(
        'inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-xs font-mono font-medium border uppercase tracking-wider',
        getStatusStyles(normalized),
        className
      )}
    >
      <span
        className={cn(
          'w-1.5 h-1.5 rounded-full',
          normalized === 'READY' && 'bg-emerald-500',
          normalized === 'PROCESSING' && 'bg-blue-500',
          normalized === 'VALIDATING' && 'bg-amber-500',
          normalized === 'FAILED' && 'bg-rose-500',
          normalized === 'UPLOADING' && 'bg-purple-500',
          normalized === 'UPLOADED' && 'bg-slate-500'
        )}
      />
      {normalized}
    </span>
  );
}
