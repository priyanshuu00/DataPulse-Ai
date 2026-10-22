export interface TrafficPoint {
  time: string;
  ingest: number;
  quality: number;
}

export interface DashboardInsight {
  id: string;
  title: string;
  description: string;
  confidenceScore: number;
  type: string;
  source: string;
}

export interface ClusterHealth {
  kafkaStatus: string;
  sparkStatus: string;
  storageStatus: string;
  storageBucket: string;
  storageEndpoint: string;
}

export interface DashboardSummary {
  totalDatasets: number;
  weeklyGrowthText: string;
  weeklyGrowthPercent: number | null;
  processingJobs: number;
  failedJobs: number;
  readyJobs: number;
  pipelineReliability: number;
  reliabilityText: string;
  storageUsedBytes: number;
  storageHumanReadable: string;
  storageBucket: string;
  ingestionTraffic: TrafficPoint[];
  insights: DashboardInsight[];
  clusterHealth: ClusterHealth;
}
