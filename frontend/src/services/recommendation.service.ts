import { apiClient } from '../lib/api/client';
import { RuleRecommendationResponse } from '../types/recommendation';

export const recommendationService = {
  getRecommendations: async (datasetId: string, versionId: string): Promise<RuleRecommendationResponse[]> => {
    const { data } = await apiClient.get<RuleRecommendationResponse[]>(
      `/datasets/${datasetId}/versions/${versionId}/recommendations`
    );
    return data;
  },

  getAllRecommendations: async (): Promise<RuleRecommendationResponse[]> => {
    const { data } = await apiClient.get<RuleRecommendationResponse[]>('/recommendations');
    return data;
  },
};
