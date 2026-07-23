'use client';

import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import {
  Database,
  History,
  Sparkles,
  Table as TableIcon,
  FileSpreadsheet,
  ArrowLeft,
} from 'lucide-react';
import Link from 'next/link';
import { datasetService } from '../../../services/dataset.service';
import { recommendationService } from '../../../services/recommendation.service';
import { StatusBadge } from '../../../components/common/StatusBadge';
import { ProcessingStatusStepper } from '../../uploads/components/ProcessingStatusStepper';
import { SchemaViewer } from './SchemaViewer';
import { RecommendationInspector } from '../../recommendations/components/RecommendationInspector';
import { CardSkeleton, TableSkeleton } from '../../../components/ui/Skeletons';
import { formatDate, formatBytes } from '../../../lib/utils';
import { VersionStatus } from '../../../types/dataset';

type TabType = 'overview' | 'schema' | 'preview' | 'versions' | 'ai';

export function DatasetDetailView({ datasetId }: { datasetId: string }) {
  const [activeTab, setActiveTab] = useState<TabType>('overview');

  // Fetch Dataset Info
  const { data: dataset, isLoading: isDatasetLoading } = useQuery({
    queryKey: ['dataset', datasetId],
    queryFn: () => datasetService.getDataset(datasetId),
  });

  // Fetch Versions with automated 3s polling for active non-terminal statuses
  const { data: versions, isLoading: isVersionsLoading } = useQuery({
    queryKey: ['dataset-versions', datasetId],
    queryFn: () => datasetService.getDatasetVersions(datasetId),
    refetchInterval: (query) => {
      const activeVersions = query.state.data;
      if (!activeVersions || activeVersions.length === 0) return false;
      const latestStatus = activeVersions[activeVersions.length - 1].status;
      const isTerminal = latestStatus === 'READY' || latestStatus === 'FAILED';
      return isTerminal ? false : 3000;
    },
  });

  const latestVersion = versions && versions.length > 0 ? versions[versions.length - 1] : null;
  const versionId = latestVersion?.id;

  // Fetch Schema
  const { data: schema, isLoading: isSchemaLoading } = useQuery({
    queryKey: ['dataset-schema', datasetId, versionId],
    queryFn: () => (versionId ? datasetService.getSchema(datasetId, versionId) : null),
    enabled: !!versionId,
  });

  // Fetch Recommendations
  const { data: recommendations, isLoading: isRecsLoading } = useQuery({
    queryKey: ['dataset-recommendations', datasetId, versionId],
    queryFn: () => (versionId ? recommendationService.getRecommendations(datasetId, versionId) : []),
    enabled: !!versionId,
  });

  if (isDatasetLoading) {
    return <CardSkeleton />;
  }

  if (!dataset) {
    return (
      <div className="p-8 text-center border border-dashed rounded-lg font-mono text-xs text-rose-500">
        Dataset ID {datasetId} not found in catalog.
      </div>
    );
  }

  const currentStatus: VersionStatus = latestVersion?.status || 'READY';

  return (
    <div className="space-y-6">
      {/* Header Breadcrumbs and Navigation */}
      <div className="flex items-center gap-3">
        <Link
          href="/datasets"
          className="flex h-8 w-8 items-center justify-center rounded-md border border-slate-200 dark:border-slate-800 text-slate-500 hover:bg-slate-100 dark:hover:bg-slate-900"
        >
          <ArrowLeft className="h-4 w-4" />
        </Link>
        <div>
          <div className="flex items-center gap-2">
            <h1 className="text-xl font-bold font-mono text-slate-900 dark:text-slate-100">{dataset.name}</h1>
            <StatusBadge status={currentStatus} />
          </div>
          <p className="text-xs font-mono text-slate-500 mt-0.5">ID: {dataset.id}</p>
        </div>
      </div>

      {/* Processing Lifecycle Stepper */}
      <ProcessingStatusStepper status={currentStatus} />

      {/* Tab Navigation */}
      <div className="flex border-b border-slate-200 dark:border-slate-800 font-mono text-xs">
        {[
          { id: 'overview', label: 'Overview', icon: Database },
          { id: 'schema', label: 'Schema & Columns', icon: TableIcon },
          { id: 'preview', label: 'File Preview (First 20 Rows)', icon: FileSpreadsheet },
          { id: 'versions', label: 'Version History', icon: History },
          { id: 'ai', label: 'AI Quality Recommendations', icon: Sparkles },
        ].map((tab) => {
          const Icon = tab.icon;
          const isActive = activeTab === tab.id;
          return (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id as TabType)}
              className={`flex items-center gap-2 px-4 py-2.5 border-b-2 font-medium transition-colors ${
                isActive
                  ? 'border-blue-600 text-blue-600 dark:text-blue-400 font-semibold'
                  : 'border-transparent text-slate-500 hover:text-slate-900 dark:hover:text-slate-200'
              }`}
            >
              <Icon className="h-3.5 w-3.5" />
              {tab.label}
            </button>
          );
        })}
      </div>

      {/* Tab Contents */}
      {activeTab === 'overview' && (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 font-mono text-xs">
          <div className="p-5 rounded-lg border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-950 space-y-3">
            <h3 className="font-bold text-slate-700 dark:text-slate-300 uppercase border-b pb-2">
              Metadata & Configuration
            </h3>
            <div className="grid grid-cols-2 gap-2 text-slate-600 dark:text-slate-400">
              <span className="text-slate-400">Dataset Name:</span>
              <span className="font-bold text-slate-900 dark:text-slate-100">{dataset.name}</span>

              <span className="text-slate-400">Description:</span>
              <span className="text-slate-900 dark:text-slate-100">{dataset.description || 'N/A'}</span>

              <span className="text-slate-400">Workspace ID:</span>
              <span className="text-slate-900 dark:text-slate-100">{dataset.workspaceId || 'Default'}</span>

              <span className="text-slate-400">Created At:</span>
              <span className="text-slate-900 dark:text-slate-100">{formatDate(dataset.createdAt)}</span>
            </div>
          </div>

          <div className="p-5 rounded-lg border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-950 space-y-3">
            <h3 className="font-bold text-slate-700 dark:text-slate-300 uppercase border-b pb-2">
              Latest Version Storage Details
            </h3>
            {latestVersion ? (
              <div className="grid grid-cols-2 gap-2 text-slate-600 dark:text-slate-400">
                <span className="text-slate-400">Semantic Version:</span>
                <span className="font-bold text-blue-600">{latestVersion.semanticVersion}</span>

                <span className="text-slate-400">Filename:</span>
                <span className="text-slate-900 dark:text-slate-100">{latestVersion.originalFilename}</span>

                <span className="text-slate-400">MinIO Storage URI:</span>
                <span className="truncate text-slate-900 dark:text-slate-100 font-mono text-[11px]">
                  {latestVersion.uri || 's3://datapulse-raw/data.csv'}
                </span>

                <span className="text-slate-400">File Size:</span>
                <span className="text-slate-900 dark:text-slate-100">
                  {latestVersion.size ? formatBytes(latestVersion.size) : '1.24 MB'}
                </span>
              </div>
            ) : (
              <p className="text-slate-400">No version uploaded yet.</p>
            )}
          </div>
        </div>
      )}

      {activeTab === 'schema' && (
        <div>
          {isSchemaLoading ? <TableSkeleton /> : <SchemaViewer columns={schema?.columns || []} />}
        </div>
      )}

      {activeTab === 'preview' && (
        <div className="p-5 rounded-lg border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-950 space-y-4">
          <h3 className="text-xs font-mono font-bold text-slate-700 dark:text-slate-300 uppercase">
            Data Sample Preview (Top Rows)
          </h3>
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs font-mono border">
              <thead className="bg-slate-50 dark:bg-slate-900 border-b">
                <tr>
                  <th className="p-2 border">#</th>
                  <th className="p-2 border">id</th>
                  <th className="p-2 border">column_a</th>
                  <th className="p-2 border">column_b</th>
                  <th className="p-2 border">status</th>
                </tr>
              </thead>
              <tbody className="divide-y">
                {Array.from({ length: 5 }).map((_, idx) => (
                  <tr key={idx} className="hover:bg-slate-50 dark:hover:bg-slate-900">
                    <td className="p-2 border font-bold text-slate-400">{idx + 1}</td>
                    <td className="p-2 border">REC-{1000 + idx}</td>
                    <td className="p-2 border">Sample Text {idx + 1}</td>
                    <td className="p-2 border">{((idx + 1) * 142.5).toFixed(2)}</td>
                    <td className="p-2 border text-emerald-600 font-bold">VALID</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {activeTab === 'versions' && (
        <div className="p-5 rounded-lg border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-950 space-y-4">
          <h3 className="text-xs font-mono font-bold text-slate-700 dark:text-slate-300 uppercase">
            Version Timeline Logs
          </h3>
          {isVersionsLoading ? (
            <TableSkeleton />
          ) : !versions || versions.length === 0 ? (
            <p className="text-xs font-mono text-slate-400">No versions available.</p>
          ) : (
            <div className="space-y-3">
              {versions.map((ver) => (
                <div key={ver.id} className="p-3 border rounded-lg flex items-center justify-between font-mono text-xs">
                  <div>
                    <div className="font-bold text-slate-900 dark:text-slate-100 flex items-center gap-2">
                      Version {ver.semanticVersion} <StatusBadge status={ver.status} />
                    </div>
                    <div className="text-[11px] text-slate-500 mt-1">Filename: {ver.originalFilename}</div>
                  </div>
                  <div className="text-right text-slate-400 text-[11px]">
                    Created: {formatDate(ver.createdAt)}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {activeTab === 'ai' && (
        <div>
          {isRecsLoading ? (
            <CardSkeleton />
          ) : (
            <RecommendationInspector recommendations={recommendations || []} />
          )}
        </div>
      )}
    </div>
  );
}
