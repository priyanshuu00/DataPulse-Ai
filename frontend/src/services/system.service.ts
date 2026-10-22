import { apiClient } from '../lib/api/client';

export interface SystemInfo {
  apiBaseUrl: string;
  storageEndpoint: string;
  storageBucket: string;
  kafkaBootstrap: string;
  appVersion: string;
  environment: string;
}

export const systemService = {
  getSystemInfo: async (): Promise<SystemInfo> => {
    const { data } = await apiClient.get<SystemInfo>('/system/info');
    return data;
  },
};
