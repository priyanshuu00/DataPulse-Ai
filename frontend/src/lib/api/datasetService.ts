import { AxiosProgressEvent } from 'axios';
import { api } from './api';
import { Dataset, DatasetVersion, DatasetSchema } from '../types/dataset';
import { RuleRecommendationResponse } from '../types/recommendation';

export const datasetService = {
  getDatasets: async (): Promise<Dataset[]> => {
    const { data } = await api.get<Dataset[]>('/datasets');
    return data;
  },
  
  getDataset: async (id: string): Promise<Dataset> => {
    const { data } = await api.get<Dataset>(`/datasets/${id}`);
    return data;
  },

  getDatasetVersions: async (id: string): Promise<DatasetVersion[]> => {
    const { data } = await api.get<DatasetVersion[]>(`/datasets/${id}/versions`);
    return data;
  },

  getDatasetVersion: async (id: string, versionId: string): Promise<DatasetVersion> => {
    const { data } = await api.get<DatasetVersion>(`/datasets/${id}/versions/${versionId}`);
    return data;
  },

  createDataset: async (
    name: string,
    description: string,
    workspaceId?: string,
    dataSourceId?: string
  ): Promise<Dataset> => {
    const { data } = await api.post<Dataset>('/datasets', {
      name,
      description,
      workspaceId,
      dataSourceId,
    });
    return data;
  },

  uploadDatasetVersion: async (
    datasetId: string,
    file: File,
    onUploadProgress?: (progressEvent: AxiosProgressEvent) => void
  ): Promise<DatasetVersion> => {
    const formData = new FormData();
    formData.append('file', file);

    const { data } = await api.post<DatasetVersion>(`/datasets/${datasetId}/versions`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
      onUploadProgress,
    });
    return data;
  },

  uploadDataset: async (
    name: string, 
    description: string, 
    file: File, 
    onUploadProgress?: (progressEvent: AxiosProgressEvent) => void
  ): Promise<DatasetVersion> => {
    const dataset = await datasetService.createDataset(name, description);
    return await datasetService.uploadDatasetVersion(dataset.id, file, onUploadProgress);
  },

  getSchema: async (id: string, versionId: string): Promise<DatasetSchema> => {
    const { data } = await api.get<DatasetSchema>(`/datasets/${id}/versions/${versionId}/schema`);
    return data;
  },

  getRecommendations: async (id: string, versionId: string): Promise<RuleRecommendationResponse[]> => {
    const { data } = await api.get<RuleRecommendationResponse[]>(`/datasets/${id}/versions/${versionId}/recommendations`);
    return data;
  },
};
