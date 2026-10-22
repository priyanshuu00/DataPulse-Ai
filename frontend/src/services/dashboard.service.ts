import { apiClient } from '../lib/api/client';
import { DashboardSummary } from '../types/dashboard';

export const dashboardService = {
  getSummary: async (): Promise<DashboardSummary> => {
    const { data } = await apiClient.get<DashboardSummary>('/dashboard/summary');
    return data;
  },
};
