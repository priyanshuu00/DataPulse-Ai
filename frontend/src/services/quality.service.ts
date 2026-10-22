import { apiClient } from '../lib/api/client';

export interface PlatformQualitySummary {
  totalRules: number;
  activeRules: number;
  averageCompliance: number;
  schemaViolations: number;
  rulesSummary: string;
}

export interface DatasetQualityReport {
  compliance: number;
  totalRules: number;
  violations: number;
  rules?: unknown[];
}

export const qualityService = {
  getPlatformQualitySummary: async (): Promise<PlatformQualitySummary> => {
    const { data } = await apiClient.get<PlatformQualitySummary>('/quality/summary');
    return data;
  },

  getDatasetQuality: async (datasetId: string): Promise<DatasetQualityReport> => {
    const { data } = await apiClient.get<DatasetQualityReport>(`/datasets/${datasetId}/quality`);
    return data;
  },
};
