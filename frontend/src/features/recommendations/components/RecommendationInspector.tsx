import { RuleRecommendationResponse } from '../../../types/recommendation';
import { Sparkles, Cpu } from 'lucide-react';
import { EmptyState } from '../../../components/common/EmptyState';

interface RecommendationInspectorProps {
  recommendations: RuleRecommendationResponse[];
}

export function RecommendationInspector({ recommendations }: RecommendationInspectorProps) {
  if (!recommendations || recommendations.length === 0) {
    return (
      <EmptyState
        title="No Recommendations Available"
        description="No deterministic rule engine or AI quality recommendations generated for this version yet."
        icon={<Sparkles className="h-6 w-6 text-purple-500" />}
      />
    );
  }

  return (
    <div className="space-y-4 font-mono">
      <div className="flex items-center justify-between">
        <h3 className="text-xs font-bold text-slate-700 dark:text-slate-300 uppercase flex items-center gap-2">
          <Sparkles className="h-4 w-4 text-purple-500" /> Quality Rule Recommendations ({recommendations.length})
        </h3>
        <span className="text-[11px] text-slate-400">Deterministic Engine & Mock LLM Synthesizer</span>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {recommendations.map((rec) => {
          const isAi = rec.source === 'AI';
          return (
            <div
              key={rec.id}
              className={`p-4 rounded-lg border space-y-3 bg-white dark:bg-slate-950 ${
                isAi ? 'border-purple-500/30' : 'border-blue-500/30'
              }`}
            >
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <span
                    className={`px-2 py-0.5 rounded text-[10px] font-bold uppercase tracking-wider flex items-center gap-1 ${
                      isAi
                        ? 'bg-purple-500/10 text-purple-600 dark:text-purple-400 border border-purple-500/20'
                        : 'bg-blue-500/10 text-blue-600 dark:text-blue-400 border border-blue-500/20'
                    }`}
                  >
                    {isAi ? <Sparkles className="h-3 w-3" /> : <Cpu className="h-3 w-3" />}
                    {rec.source}
                  </span>
                  <span className="text-xs font-bold text-slate-900 dark:text-slate-100">
                    {rec.ruleType}
                  </span>
                </div>
                <span className="text-[11px] text-slate-500">
                  Column: <strong className="text-slate-900 dark:text-slate-100">{rec.columnName}</strong>
                </span>
              </div>

              <p className="text-xs text-slate-600 dark:text-slate-300 font-sans leading-relaxed">
                {rec.explanation}
              </p>

              {/* Confidence Score Bar */}
              <div className="space-y-1">
                <div className="flex justify-between text-[10px] text-slate-500">
                  <span>Confidence Score</span>
                  <span className="font-bold text-slate-700 dark:text-slate-300">
                    {(rec.confidenceScore * 100).toFixed(0)}%
                  </span>
                </div>
                <div className="h-1.5 w-full bg-slate-100 dark:bg-slate-800 rounded-full overflow-hidden">
                  <div
                    className={`h-full rounded-full ${isAi ? 'bg-purple-500' : 'bg-blue-500'}`}
                    style={{ width: `${rec.confidenceScore * 100}%` }}
                  />
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
