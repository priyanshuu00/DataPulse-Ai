'use client';

import { useQuery } from '@tanstack/react-query';
import { Cpu, Layers, Server, RefreshCw } from 'lucide-react';
import { StatusBadge } from '../../../components/common/StatusBadge';
import { pipelineService } from '../../../services/pipeline.service';
import { TableSkeleton } from '../../../components/ui/Skeletons';

export function PipelineMonitorView() {
  const { data: jobs, isLoading, refetch, isFetching } = useQuery({
    queryKey: ['pipeline-jobs'],
    queryFn: pipelineService.getJobs,
    refetchInterval: 4000,
  });

  const { data: clusterHealth } = useQuery({
    queryKey: ['pipeline-cluster'],
    queryFn: pipelineService.getClusterHealth,
    refetchInterval: 5000,
  });

  const activeJobsList = jobs || [];

  return (
    <div className="space-y-6 font-mono">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 border-b border-slate-200 dark:border-slate-800 pb-4">
        <div>
          <h1 className="text-xl font-bold tracking-tight text-slate-900 dark:text-slate-100">
            ASYNCHRONOUS PIPELINE MONITOR
          </h1>
          <p className="text-xs text-slate-500 mt-0.5">
            Distributed execution telemetry: Kafka Event Producer → Spark Processing Cluster → PostgreSQL Storage.
          </p>
        </div>

        <button 
          onClick={() => refetch()}
          disabled={isFetching}
          className="inline-flex items-center gap-2 bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 text-slate-700 dark:text-slate-300 text-xs font-semibold px-3 py-1.5 rounded border transition-colors disabled:opacity-50"
        >
          <RefreshCw className={`h-3.5 w-3.5 ${isFetching ? 'animate-spin' : ''}`} /> Refresh Telemetry
        </button>
      </div>

      {/* Cluster Health Panel */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="p-4 rounded-lg border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-950 space-y-2">
          <div className="flex items-center justify-between text-xs text-slate-500">
            <span className="flex items-center gap-1.5">
              <Server className="h-4 w-4 text-blue-500" /> KAFKA EVENT BROKER
            </span>
            <span className="text-emerald-500 text-[10px] font-bold">
              {clusterHealth?.kafkaStatus || 'HEALTHY'}
            </span>
          </div>
          <div className="text-lg font-bold text-slate-900 dark:text-slate-100">
            datapulse.datasets.events
          </div>
          <div className="text-[11px] text-slate-400">Partition: 0 | Event Stream Broker</div>
        </div>

        <div className="p-4 rounded-lg border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-950 space-y-2">
          <div className="flex items-center justify-between text-xs text-slate-500">
            <span className="flex items-center gap-1.5">
              <Cpu className="h-4 w-4 text-amber-500" /> SPARK ENGINE WORKER
            </span>
            <span className={`text-[10px] font-bold ${clusterHealth?.sparkStatus?.includes('ACTIVE') ? 'text-amber-500' : 'text-emerald-500'}`}>
              {clusterHealth?.sparkStatus || 'STANDBY'}
            </span>
          </div>
          <div className="text-lg font-bold text-slate-900 dark:text-slate-100">
            SparkWorkerApp
          </div>
          <div className="text-[11px] text-slate-400">Distributed Ingestion & Schema Extraction</div>
        </div>

        <div className="p-4 rounded-lg border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-950 space-y-2">
          <div className="flex items-center justify-between text-xs text-slate-500">
            <span className="flex items-center gap-1.5">
              <Layers className="h-4 w-4 text-purple-500" /> MINIO OBJECT STORAGE
            </span>
            <span className="text-emerald-500 text-[10px] font-bold">
              {clusterHealth?.storageStatus || 'ONLINE'}
            </span>
          </div>
          <div className="text-lg font-bold text-slate-900 dark:text-slate-100">
            {clusterHealth?.storageBucket || 'datapulse-raw'}
          </div>
          <div className="text-[11px] text-slate-400">Endpoint: {clusterHealth?.storageEndpoint || 'http://localhost:9000'}</div>
        </div>
      </div>

      {/* Active Pipeline Jobs Table */}
      <div className="rounded-lg border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-950 overflow-hidden">
        <div className="p-4 border-b border-slate-200 dark:border-slate-800 flex items-center justify-between">
          <h3 className="text-xs font-bold text-slate-700 dark:text-slate-300 uppercase">
            Active Job Execution Registry ({activeJobsList.length})
          </h3>
        </div>

        <div className="overflow-x-auto">
          {isLoading ? (
            <div className="p-4">
              <TableSkeleton rows={4} />
            </div>
          ) : activeJobsList.length === 0 ? (
            <div className="p-8 text-center text-slate-500 text-xs">
              No pipeline execution records in registry. Ingest a dataset version to initiate processing telemetry.
            </div>
          ) : (
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-50 dark:bg-slate-900 text-slate-500 uppercase border-b">
                <tr>
                  <th className="p-3">Job ID</th>
                  <th className="p-3">Target Dataset</th>
                  <th className="p-3">Version</th>
                  <th className="p-3">Current Stage</th>
                  <th className="p-3">Kafka Status</th>
                  <th className="p-3">Spark Execution</th>
                  <th className="p-3">Duration</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-200 dark:divide-slate-800">
                {activeJobsList.map((job) => (
                  <tr key={job.id} className="hover:bg-slate-50 dark:hover:bg-slate-900/50">
                    <td className="p-3 font-bold text-blue-600">{job.id}</td>
                    <td className="p-3 font-semibold text-slate-900 dark:text-slate-100">{job.datasetName}</td>
                    <td className="p-3 text-slate-500">{job.version}</td>
                    <td className="p-3">
                      <StatusBadge status={job.stage as any} />
                    </td>
                    <td className={`p-3 font-bold ${job.kafkaStatus === 'ERROR' ? 'text-rose-600' : 'text-emerald-600'}`}>
                      {job.kafkaStatus}
                    </td>
                    <td className={`p-3 font-bold ${job.sparkStatus === 'RUNNING' ? 'text-amber-600' : (job.sparkStatus === 'FAILED' ? 'text-rose-600' : 'text-emerald-600')}`}>
                      {job.sparkStatus}
                    </td>
                    <td className="p-3 text-slate-500">{job.duration}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>
    </div>
  );
}
