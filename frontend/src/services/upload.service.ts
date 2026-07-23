import { AxiosProgressEvent } from 'axios';
import { apiClient } from '../lib/api/client';
import { DatasetVersion } from '../types/dataset';

export const uploadService = {
  uploadVersion: async (
    datasetId: string,
    file: File,
    onUploadProgress?: (progressEvent: AxiosProgressEvent) => void
  ): Promise<DatasetVersion> => {
    const formData = new FormData();
    formData.append('file', file);

    const { data } = await apiClient.post<DatasetVersion>(`/datasets/${datasetId}/versions`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
      onUploadProgress,
    });
    return data;
  },
};
