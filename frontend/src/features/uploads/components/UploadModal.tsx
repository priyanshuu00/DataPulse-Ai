'use client';

import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { FileUp, Loader2, X } from 'lucide-react';
import { toast } from 'sonner';
import { AxiosError } from 'axios';
import { datasetService } from '../../../services/dataset.service';
import { uploadService } from '../../../services/upload.service';
import { DatasetVersion } from '../../../types/dataset';

const uploadSchema = z.object({
  name: z.string().min(2, 'Dataset name must be at least 2 characters'),
  description: z.string().optional(),
});

type UploadFormValues = z.infer<typeof uploadSchema>;

interface UploadModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSuccess?: (version: DatasetVersion) => void;
}

export function UploadModal({ isOpen, onClose, onSuccess }: UploadModalProps) {
  const [file, setFile] = useState<File | null>(null);
  const [progress, setProgress] = useState<number>(0);
  const [isUploading, setIsUploading] = useState<boolean>(false);
  const [dragActive, setDragActive] = useState<boolean>(false);

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<UploadFormValues>({
    resolver: zodResolver(uploadSchema),
  });

  if (!isOpen) return null;

  const handleDrag = (e: React.DragEvent) => {
    e.preventDefault();
    e.stopPropagation();
    if (e.type === 'dragenter' || e.type === 'dragover') {
      setDragActive(true);
    } else if (e.type === 'dragleave') {
      setDragActive(false);
    }
  };

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive(false);
    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      validateAndSetFile(e.dataTransfer.files[0]);
    }
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      validateAndSetFile(e.target.files[0]);
    }
  };

  const validateAndSetFile = (f: File) => {
    const validExtensions = ['.csv', '.json', '.parquet'];
    const ext = f.name.substring(f.name.lastIndexOf('.')).toLowerCase();
    if (!validExtensions.includes(ext)) {
      toast.error(`Invalid file type. Supported: ${validExtensions.join(', ')}`);
      return;
    }
    if (f.size > 500 * 1024 * 1024) { // 500MB max limit
      toast.error('File size exceeds maximum allowed limit of 500MB');
      return;
    }
    setFile(f);
  };

  const onSubmit = async (values: UploadFormValues) => {
    if (!file) {
      toast.error('Please select a file to upload');
      return;
    }

    try {
      setIsUploading(true);
      setProgress(10);
      toast.info('Creating dataset entity...');

      const dataset = await datasetService.createDataset({
        name: values.name,
        description: values.description || '',
      });

      toast.info('Uploading binary dataset version...');
      const version = await uploadService.uploadVersion(dataset.id, file, (evt) => {
        if (evt.total) {
          const pct = Math.round((evt.loaded * 100) / evt.total);
          setProgress(pct);
        }
      });

      toast.success(`Successfully uploaded ${file.name}`);
      reset();
      setFile(null);
      setIsUploading(false);
      onClose();
      if (onSuccess) onSuccess(version);
    } catch (err: unknown) {
      setIsUploading(false);
      const errorMsg = (err as AxiosError<{ message?: string }>)?.response?.data?.message || 'Failed to upload dataset';
      toast.error(errorMsg);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/60 backdrop-blur-sm p-4">
      <div className="w-full max-w-md rounded-lg border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-950 p-6 shadow-xl">
        <div className="flex items-center justify-between border-b border-slate-200 dark:border-slate-800 pb-3 mb-4">
          <div className="flex items-center gap-2">
            <FileUp className="h-5 w-5 text-blue-600" />
            <h3 className="text-sm font-bold text-slate-900 dark:text-slate-100 uppercase font-mono">
              Ingest New Dataset
            </h3>
          </div>
          <button
            onClick={onClose}
            disabled={isUploading}
            className="text-slate-400 hover:text-slate-600 dark:hover:text-slate-200"
          >
            <X className="h-4 w-4" />
          </button>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div>
            <label className="block text-xs font-mono font-medium text-slate-700 dark:text-slate-300 mb-1">
              Dataset Name *
            </label>
            <input
              {...register('name')}
              type="text"
              placeholder="e.g. Customer Sales Q3"
              className="w-full rounded border border-slate-300 dark:border-slate-800 bg-slate-50 dark:bg-slate-900 px-3 py-1.5 text-xs text-slate-900 dark:text-slate-100 focus:outline-none focus:ring-1 focus:ring-blue-500"
            />
            {errors.name && (
              <p className="mt-1 text-[11px] text-rose-500">{errors.name.message}</p>
            )}
          </div>

          <div>
            <label className="block text-xs font-mono font-medium text-slate-700 dark:text-slate-300 mb-1">
              Description
            </label>
            <textarea
              {...register('description')}
              rows={2}
              placeholder="Provide context regarding this data source..."
              className="w-full rounded border border-slate-300 dark:border-slate-800 bg-slate-50 dark:bg-slate-900 px-3 py-1.5 text-xs text-slate-900 dark:text-slate-100 focus:outline-none focus:ring-1 focus:ring-blue-500"
            />
          </div>

          {/* Drag & Drop Target */}
          <div
            onDragEnter={handleDrag}
            onDragOver={handleDrag}
            onDragLeave={handleDrag}
            onDrop={handleDrop}
            className={`flex flex-col items-center justify-center p-6 border-2 border-dashed rounded-lg text-center cursor-pointer transition-colors ${
              dragActive
                ? 'border-blue-500 bg-blue-50/50 dark:bg-blue-950/20'
                : 'border-slate-300 dark:border-slate-800 bg-slate-50/50 dark:bg-slate-900/30'
            }`}
          >
            <input
              type="file"
              accept=".csv,.json,.parquet"
              onChange={handleFileChange}
              className="hidden"
              id="file-upload-input"
            />
            <label htmlFor="file-upload-input" className="cursor-pointer flex flex-col items-center">
              <FileUp className="h-8 w-8 text-slate-400 mb-2" />
              {file ? (
                <div className="text-xs font-mono text-emerald-600 dark:text-emerald-400 font-semibold">
                  Selected: {file.name} ({(file.size / (1024 * 1024)).toFixed(2)} MB)
                </div>
              ) : (
                <>
                  <span className="text-xs font-medium text-slate-700 dark:text-slate-300">
                    Drag & drop dataset file or <span className="text-blue-600 underline">browse</span>
                  </span>
                  <span className="text-[10px] font-mono text-slate-400 mt-1">
                    Supported: CSV, JSON, PARQUET (Max 500MB)
                  </span>
                </>
              )}
            </label>
          </div>

          {/* Upload Progress Bar */}
          {isUploading && (
            <div className="space-y-1.5">
              <div className="flex justify-between text-[11px] font-mono text-slate-500">
                <span>Ingesting to MinIO & Kafka...</span>
                <span>{progress}%</span>
              </div>
              <div className="h-2 w-full bg-slate-200 dark:bg-slate-800 rounded-full overflow-hidden">
                <div
                  className="h-full bg-blue-600 transition-all duration-300"
                  style={{ width: `${progress}%` }}
                />
              </div>
            </div>
          )}

          <div className="flex justify-end gap-2 pt-2 border-t border-slate-200 dark:border-slate-800">
            <button
              type="button"
              onClick={onClose}
              disabled={isUploading}
              className="px-3 py-1.5 text-xs font-medium text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-900 rounded"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={isUploading || !file}
              className="flex items-center gap-1.5 px-4 py-1.5 text-xs font-semibold bg-blue-600 text-white rounded hover:bg-blue-700 disabled:opacity-50"
            >
              {isUploading ? <Loader2 className="h-3.5 w-3.5 animate-spin" /> : null}
              Start Ingestion
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
