import { apiClient } from '../lib/api/client';
import { PipelineJob, ClusterHealth } from '../types/pipeline';

export const pipelineService = {
  getJobs: async (): Promise<PipelineJob[]> => {
    const { data } = await apiClient.get<PipelineJob[]>('/pipeline/jobs');
    return data;
  },

  getClusterHealth: async (): Promise<ClusterHealth> => {
    const { data } = await apiClient.get<ClusterHealth>('/pipeline/cluster');
    return data;
  },
};
