import { VersionStatus } from '../../../types/dataset';
import { CheckCircle2, Clock, AlertCircle, Loader2 } from 'lucide-react';
import { cn } from '../../../lib/utils';

interface ProcessingStatusStepperProps {
  status: VersionStatus;
}

const STAGES: VersionStatus[] = ['UPLOADING', 'UPLOADED', 'VALIDATING', 'PROCESSING', 'READY'];

export function ProcessingStatusStepper({ status }: ProcessingStatusStepperProps) {
  const isFailed = status === 'FAILED';
  const currentIndex = isFailed ? STAGES.indexOf('PROCESSING') : STAGES.indexOf(status);

  return (
    <div className="w-full p-4 rounded-lg border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-950">
      <div className="flex items-center justify-between mb-4">
        <h4 className="text-xs font-mono font-bold tracking-wider text-slate-700 dark:text-slate-300 uppercase">
          Ingestion & Processing Lifecycle
        </h4>
        <span
          className={cn(
            'text-xs font-mono px-2 py-0.5 rounded font-semibold',
            status === 'READY' && 'bg-emerald-500/10 text-emerald-600',
            status === 'FAILED' && 'bg-rose-500/10 text-rose-600',
            status !== 'READY' && status !== 'FAILED' && 'bg-blue-500/10 text-blue-600 animate-pulse'
          )}
        >
          {status}
        </span>
      </div>

      <div className="grid grid-cols-5 gap-2 relative">
        {STAGES.map((stage, idx) => {
          const isComplete = !isFailed && idx <= currentIndex;
          const isCurrent = idx === currentIndex;

          return (
            <div key={stage} className="flex flex-col items-center text-center space-y-2 relative">
              <div
                className={cn(
                  'flex h-8 w-8 items-center justify-center rounded-full border text-xs font-mono transition-colors',
                  isComplete
                    ? 'bg-emerald-600 border-emerald-600 text-white'
                    : isCurrent && isFailed
                    ? 'bg-rose-600 border-rose-600 text-white'
                    : isCurrent
                    ? 'bg-blue-600 border-blue-600 text-white'
                    : 'bg-slate-100 dark:bg-slate-900 border-slate-300 dark:border-slate-800 text-slate-400'
                )}
              >
                {isComplete ? (
                  <CheckCircle2 className="h-4 w-4" />
                ) : isCurrent && isFailed ? (
                  <AlertCircle className="h-4 w-4" />
                ) : isCurrent ? (
                  <Loader2 className="h-4 w-4 animate-spin" />
                ) : (
                  <Clock className="h-3.5 w-3.5" />
                )}
              </div>
              <span className="text-[11px] font-mono font-medium text-slate-600 dark:text-slate-400">
                {stage}
              </span>
            </div>
          );
        })}
      </div>
    </div>
  );
}
