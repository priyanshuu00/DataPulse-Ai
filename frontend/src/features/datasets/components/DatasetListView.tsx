'use client';

import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Search, Filter, Plus, FileSpreadsheet, ArrowUpDown } from 'lucide-react';
import { datasetService } from '../../../services/dataset.service';
import { StatusBadge } from '../../../components/common/StatusBadge';
import { TableSkeleton } from '../../../components/ui/Skeletons';
import { EmptyState } from '../../../components/common/EmptyState';
import Link from 'next/link';
import { formatDate } from '../../../lib/utils';

export function DatasetListView({ onOpenUpload }: { onOpenUpload: () => void }) {
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');

  const { data: datasets, isLoading, isError, refetch } = useQuery({
    queryKey: ['datasets'],
    queryFn: datasetService.getDatasets,
  });

  const filteredDatasets = (datasets || []).filter((ds) => {
    const matchesSearch = ds.name.toLowerCase().includes(search.toLowerCase()) || ds.id.toLowerCase().includes(search.toLowerCase());
    const matchesStatus = statusFilter === 'ALL' || (ds.status || 'READY') === statusFilter;
    return matchesSearch && matchesStatus;
  });

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 border-b border-slate-200 dark:border-slate-800 pb-4">
        <div>
          <h1 className="text-xl font-bold tracking-tight text-slate-900 dark:text-slate-100 font-mono">
            DATASET REGISTRY & CATALOG
          </h1>
          <p className="text-xs text-slate-500 font-mono mt-0.5">
            Enterprise metadata repository and cataloged storage assets.
          </p>
        </div>

        <button
          onClick={onOpenUpload}
          className="inline-flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white text-xs font-semibold px-4 py-2 rounded-md shadow-sm transition-colors"
        >
          <Plus className="h-4 w-4" />
          Ingest Dataset
        </button>
      </div>

      {/* Filter and Search Bar */}
      <div className="flex flex-col sm:flex-row items-center justify-between gap-4">
        <div className="relative w-full sm:w-80">
          <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-slate-400" />
          <input
            type="text"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search datasets by name or UUID..."
            className="h-9 w-full rounded-md border border-slate-300 dark:border-slate-800 bg-white dark:bg-slate-900 pl-9 pr-3 text-xs text-slate-900 dark:text-slate-100 placeholder:text-slate-400 focus:outline-none focus:ring-1 focus:ring-blue-500"
          />
        </div>

        <div className="flex items-center gap-3 w-full sm:w-auto justify-end">
          <div className="flex items-center gap-1.5 text-xs text-slate-500 font-mono">
            <Filter className="h-3.5 w-3.5" /> Filter:
          </div>
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="h-9 rounded-md border border-slate-300 dark:border-slate-800 bg-white dark:bg-slate-900 px-3 text-xs font-mono text-slate-900 dark:text-slate-100 focus:outline-none focus:ring-1 focus:ring-blue-500"
          >
            <option value="ALL">All Statuses</option>
            <option value="READY">READY</option>
            <option value="PROCESSING">PROCESSING</option>
            <option value="FAILED">FAILED</option>
          </select>
        </div>
      </div>

      {/* Table Content */}
      <div className="rounded-lg border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-950 overflow-hidden shadow-sm">
        {isLoading ? (
          <div className="p-4">
            <TableSkeleton rows={6} />
          </div>
        ) : isError ? (
          <div className="p-8 text-center space-y-3">
            <p className="text-xs text-rose-500 font-mono">Failed to load datasets from backend server.</p>
            <button
              onClick={() => refetch()}
              className="px-3 py-1.5 text-xs font-mono bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 rounded"
            >
              Retry Connection
            </button>
          </div>
        ) : filteredDatasets.length === 0 ? (
          <EmptyState
            title="No Datasets Found"
            description="No dataset records match your criteria or repository is empty."
            action={
              <button
                onClick={onOpenUpload}
                className="px-3 py-1.5 text-xs font-semibold bg-blue-600 text-white rounded hover:bg-blue-700"
              >
                Upload First Dataset
              </button>
            }
          />
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs font-mono">
              <thead className="bg-slate-50 dark:bg-slate-900 text-slate-500 uppercase border-b border-slate-200 dark:border-slate-800">
                <tr>
                  <th className="p-3">
                    <span className="flex items-center gap-1 cursor-pointer">
                      Dataset Name <ArrowUpDown className="h-3 w-3" />
                    </span>
                  </th>
                  <th className="p-3">Dataset ID</th>
                  <th className="p-3">Created At</th>
                  <th className="p-3">Updated At</th>
                  <th className="p-3">Status</th>
                  <th className="p-3 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-200 dark:divide-slate-800">
                {filteredDatasets.map((ds) => (
                  <tr key={ds.id} className="hover:bg-slate-50 dark:hover:bg-slate-900/50">
                    <td className="p-3 font-semibold text-slate-900 dark:text-slate-100 flex items-center gap-2">
                      <FileSpreadsheet className="h-4 w-4 text-blue-500 shrink-0" />
                      <div>
                        <div>{ds.name}</div>
                        {ds.description && (
                          <div className="text-[10px] text-slate-400 font-sans font-normal truncate max-w-xs">
                            {ds.description}
                          </div>
                        )}
                      </div>
                    </td>
                    <td className="p-3 text-slate-500 font-mono text-[11px]">{ds.id}</td>
                    <td className="p-3 text-slate-500">{formatDate(ds.createdAt)}</td>
                    <td className="p-3 text-slate-500">{formatDate(ds.updatedAt)}</td>
                    <td className="p-3">
                      <StatusBadge status={ds.status || 'READY'} />
                    </td>
                    <td className="p-3 text-right">
                      <Link
                        href={`/datasets/${ds.id}`}
                        className="text-blue-600 hover:underline font-bold text-[11px]"
                      >
                        Inspect Details →
                      </Link>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
