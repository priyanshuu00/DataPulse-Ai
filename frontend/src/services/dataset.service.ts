import { apiClient } from '../lib/api/client';
import {
  Dataset,
  DatasetVersion,
  DatasetSchema,
  DatasetCreateInput,
} from '../types/dataset';

export interface ActiveJobsResponse {
  activeJobs: number;
}

export const datasetService = {
  getDatasets: async (): Promise<Dataset[]> => {
    const { data } = await apiClient.get<Dataset[]>('/datasets');
    return data;
  },

  getActiveJobs: async (): Promise<ActiveJobsResponse> => {
    const { data } = await apiClient.get<ActiveJobsResponse>(
      '/datasets/active-jobs'
    );
    return data;
  },

  getDataset: async (id: string): Promise<Dataset> => {
    const { data } = await apiClient.get<Dataset>(`/datasets/${id}`);
    return data;
  },

  createDataset: async (input: DatasetCreateInput): Promise<Dataset> => {
    const payload = {
      name: input.name,
      description: input.description || '',
      workspaceId:
        input.workspaceId || '00000000-0000-0000-0000-000000000001',
      dataSourceId:
        input.dataSourceId || '00000000-0000-0000-0000-000000000002',
    };

    const { data } = await apiClient.post<Dataset>('/datasets', payload);
    return data;
  },

  getDatasetVersions: async (id: string): Promise<DatasetVersion[]> => {
    const { data } = await apiClient.get<DatasetVersion[]>(
      `/datasets/${id}/versions`
    );
    return data;
  },

  getDatasetVersion: async (
    id: string,
    versionId: string
  ): Promise<DatasetVersion> => {
    const { data } = await apiClient.get<DatasetVersion>(
      `/datasets/${id}/versions/${versionId}`
    );
    return data;
  },

  getSchema: async (
    id: string,
    versionId: string
  ): Promise<DatasetSchema> => {
    const { data } = await apiClient.get<DatasetSchema>(
      `/datasets/${id}/versions/${versionId}/schema`
    );
    return data;
  },

  deleteDataset: async (id: string): Promise<void> => {
    await apiClient.delete(`/datasets/${id}`);
  },
};
