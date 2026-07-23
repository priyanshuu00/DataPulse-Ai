import { ColumnMetadata } from '../../../types/dataset';
import { Table, FileCode } from 'lucide-react';

interface SchemaViewerProps {
  columns: ColumnMetadata[];
}

export function SchemaViewer({ columns }: SchemaViewerProps) {
  if (!columns || columns.length === 0) {
    return (
      <div className="p-8 text-center border border-dashed rounded-lg text-xs font-mono text-slate-400">
        No schema columns extracted yet for this dataset version.
      </div>
    );
  }

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between text-xs font-mono text-slate-500">
        <span className="flex items-center gap-1.5 font-bold text-slate-700 dark:text-slate-300">
          <Table className="h-4 w-4 text-blue-500" /> Extracted Columns ({columns.length})
        </span>
        <span>PostgreSQL & Spark Inferred</span>
      </div>

      <div className="overflow-x-auto rounded-lg border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-950">
        <table className="w-full text-left text-xs font-mono">
          <thead className="bg-slate-50 dark:bg-slate-900 text-slate-500 uppercase border-b border-slate-200 dark:border-slate-800">
            <tr>
              <th className="p-2.5">Column Name</th>
              <th className="p-2.5">Data Type</th>
              <th className="p-2.5">Nullable</th>
              <th className="p-2.5">Null Count</th>
              <th className="p-2.5">Sample Values</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-200 dark:divide-slate-800">
            {columns.map((col) => (
              <tr key={col.id || col.name} className="hover:bg-slate-50 dark:hover:bg-slate-900/50">
                <td className="p-2.5 font-semibold text-slate-900 dark:text-slate-100 flex items-center gap-1.5">
                  <FileCode className="h-3.5 w-3.5 text-slate-400" />
                  {col.name}
                </td>
                <td className="p-2.5">
                  <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-blue-500/10 text-blue-600 dark:text-blue-400 border border-blue-500/20">
                    {col.dataType}
                  </span>
                </td>
                <td className="p-2.5 text-slate-500">{col.isNullable ? 'YES' : 'NO (NOT NULL)'}</td>
                <td className="p-2.5 text-slate-500">{col.nullCount !== undefined && col.nullCount !== null ? col.nullCount : '0'}</td>
                <td className="p-2.5 text-slate-400 truncate max-w-xs font-mono text-[11px]">
                  {col.sampleValues || 'N/A'}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
