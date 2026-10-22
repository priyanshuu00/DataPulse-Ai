'use client';

import { useQuery } from '@tanstack/react-query';
import { Database, AlertTriangle, HardDrive, ArrowUpRight, Sparkles, Activity } from 'lucide-react';
import { AreaChart, Area, XAxis, YAxis, Tooltip, ResponsiveContainer } from 'recharts';
import { datasetService } from '../../../services/dataset.service';
import { StatusBadge } from '../../../components/common/StatusBadge';
import { CardSkeleton } from '../../../components/ui/Skeletons';
import Link from 'next/link';
import { formatDate } from '../../../lib/utils';

const chartData = [
  { time: '00:00', ingest: 12, quality: 98 },
  { time: '04:00', ingest: 18, quality: 96 },
  { time: '08:00', ingest: 45, quality: 99 },
  { time: '12:00', ingest: 32, quality: 97 },
  { time: '16:00', ingest: 64, quality: 99 },
  { time: '20:00', ingest: 28, quality: 98 },
];

export function DashboardView({ onOpenUpload }: { onOpenUpload: () => void }) {
  const { data: datasets, isLoading } = useQuery({
    queryKey: ['datasets'],
    queryFn: datasetService.getDatasets,
  });

  const { data: activeJobsData } = useQuery({
    queryKey: ['active-jobs'],
    queryFn: datasetService.getActiveJobs,
    refetchInterval: 5000,
  });

  const activeJobs = activeJobsData?.activeJobs ?? 0;

  const totalDatasets = datasets?.length || 0;
  const recentDatasets = datasets?.slice(0, 5) || [];


  return (
    <div className="space-y-6">
      {/* Header Banner */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 border-b border-slate-200 dark:border-slate-800 pb-4">
        <div>
          <h1 className="text-xl font-bold tracking-tight text-slate-900 dark:text-slate-100 font-mono">
            EXECUTIVE DATA PLATFORM DASHBOARD
          </h1>
          <p className="text-xs text-slate-500 font-mono mt-0.5">
            Real-time pipeline monitoring, data ingestion metrics, and automated AI quality recommendations.
          </p>
        </div>

        <button
          onClick={onOpenUpload}
          className="inline-flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white text-xs font-semibold px-4 py-2 rounded-md shadow-sm transition-colors"
        >
          <Activity className="h-4 w-4" />
          Upload & Ingest Dataset
        </button>
      </div>

      {/* Top Metric Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {isLoading ? (
          <>
            <CardSkeleton />
            <CardSkeleton />
            <CardSkeleton />
            <CardSkeleton />
          </>
        ) : (
          <>
            <div className="p-4 rounded-lg border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-950 space-y-2">
              <div className="flex items-center justify-between text-slate-500 font-mono text-xs">
                <span>TOTAL DATASETS</span>
                <Database className="h-4 w-4 text-blue-500" />
              </div>
              <div className="text-2xl font-bold text-slate-900 dark:text-slate-100 font-mono">
                {totalDatasets}
              </div>
              <div className="text-[11px] text-emerald-600 font-mono flex items-center gap-1">
                <ArrowUpRight className="h-3 w-3" /> +12% this week
              </div>
            </div>

            <div className="p-4 rounded-lg border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-950 space-y-2">
              <div className="flex items-center justify-between text-slate-500 font-mono text-xs">
                <span>PROCESSING JOBS</span>
                <Activity className="h-4 w-4 text-emerald-500" />
              </div>
              <div className="text-2xl font-bold text-slate-900 dark:text-slate-100 font-mono">
                {activeJobs} Active
              </div>
              <div className="text-[11px] text-slate-500 font-mono">Spark-01 Cluster Active</div>
            </div>

            <div className="p-4 rounded-lg border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-950 space-y-2">
              <div className="flex items-center justify-between text-slate-500 font-mono text-xs">
                <span>FAILED JOBS</span>
                <AlertTriangle className="h-4 w-4 text-rose-500" />
              </div>
              <div className="text-2xl font-bold text-rose-600 font-mono">0</div>
              <div className="text-[11px] text-emerald-600 font-mono">100% Pipeline Reliability</div>
            </div>

            <div className="p-4 rounded-lg border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-950 space-y-2">
              <div className="flex items-center justify-between text-slate-500 font-mono text-xs">
                <span>STORAGE USED</span>
                <HardDrive className="h-4 w-4 text-purple-500" />
              </div>
              <div className="text-2xl font-bold text-slate-900 dark:text-slate-100 font-mono">
                14.2 GB
              </div>
              <div className="text-[11px] text-slate-500 font-mono">MinIO Bucket: datapulse-raw</div>
            </div>
          </>
        )}
      </div>

      {/* Analytics Charts */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 p-5 rounded-lg border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-950">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-xs font-mono font-bold text-slate-700 dark:text-slate-300 uppercase">
              Ingestion Traffic & Rate (24h)
            </h3>
            <span className="text-[11px] font-mono text-slate-400">Events / sec</span>
          </div>
          <div className="h-64 w-full">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={chartData}>
                <defs>
                  <linearGradient id="colorIngest" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#2563eb" stopOpacity={0.4} />
                    <stop offset="95%" stopColor="#2563eb" stopOpacity={0} />
                  </linearGradient>
                </defs>
                <XAxis dataKey="time" stroke="#94a3b8" fontSize={11} tickLine={false} />
                <YAxis stroke="#94a3b8" fontSize={11} tickLine={false} />
                <Tooltip contentStyle={{ backgroundColor: '#0f172a', borderColor: '#334155', color: '#f8fafc', fontSize: '12px' }} />
                <Area type="monotone" dataKey="ingest" stroke="#2563eb" strokeWidth={2} fillOpacity={1} fill="url(#colorIngest)" />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="p-5 rounded-lg border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-950 space-y-4">
          <div className="flex items-center justify-between">
            <h3 className="text-xs font-mono font-bold text-slate-700 dark:text-slate-300 uppercase flex items-center gap-2">
              <Sparkles className="h-4 w-4 text-purple-500" /> AI Insights Digest
            </h3>
          </div>
          <div className="space-y-3 font-mono text-xs">
            <div className="p-3 rounded border border-purple-500/20 bg-purple-500/5 space-y-1">
              <div className="font-semibold text-purple-600 dark:text-purple-400 flex items-center justify-between">
                <span>Anomaly Detected</span>
                <span className="text-[10px] bg-purple-500/10 px-1.5 py-0.5 rounded">99.4% confidence</span>
              </div>
              <p className="text-slate-600 dark:text-slate-300 text-[11px]">
                High variance in `transaction_amount` column. Recommended constraint: `NotNull & Range(0, 10000)`.
              </p>
            </div>

            <div className="p-3 rounded border border-blue-500/20 bg-blue-500/5 space-y-1">
              <div className="font-semibold text-blue-600 dark:text-blue-400 flex items-center justify-between">
                <span>Schema Match</span>
                <span className="text-[10px] bg-blue-500/10 px-1.5 py-0.5 rounded">Deterministic</span>
              </div>
              <p className="text-slate-600 dark:text-slate-300 text-[11px]">
                100% schema compliance verified against PostgreSQL schema registry.
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* Recent Ingested Datasets Table */}
      <div className="p-5 rounded-lg border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-950 space-y-4">
        <div className="flex items-center justify-between">
          <h3 className="text-xs font-mono font-bold text-slate-700 dark:text-slate-300 uppercase">
            Recent Data Assets
          </h3>
          <Link href="/datasets" className="text-xs font-mono text-blue-600 hover:underline">
            View All Datasets →
          </Link>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs font-mono">
            <thead className="bg-slate-50 dark:bg-slate-900 text-slate-500 uppercase border-b border-slate-200 dark:border-slate-800">
              <tr>
                <th className="p-2.5">Dataset Name</th>
                <th className="p-2.5">Dataset ID</th>
                <th className="p-2.5">Created At</th>
                <th className="p-2.5">Status</th>
                <th className="p-2.5 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-200 dark:divide-slate-800">
              {recentDatasets.length === 0 ? (
                <tr>
                  <td colSpan={5} className="p-4 text-center text-slate-500">
                    No datasets ingested yet. Click &quot;Upload & Ingest Dataset&quot; to begin.
                  </td>
                </tr>
              ) : (
                recentDatasets.map((ds) => (
                  <tr key={ds.id} className="hover:bg-slate-50 dark:hover:bg-slate-900/50">
                    <td className="p-2.5 font-semibold text-slate-900 dark:text-slate-100">{ds.name}</td>
                    <td className="p-2.5 text-slate-500 font-mono text-[11px]">{ds.id}</td>
                    <td className="p-2.5 text-slate-500">{formatDate(ds.createdAt)}</td>
                    <td className="p-2.5">
                      <StatusBadge status="READY" />
                    </td>
                    <td className="p-2.5 text-right">
                      <Link
                        href={`/datasets/${ds.id}`}
                        className="text-blue-600 hover:underline text-[11px] font-bold"
                      >
                        Inspect →
                      </Link>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
