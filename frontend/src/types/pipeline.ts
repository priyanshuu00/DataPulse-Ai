export interface PipelineJob {
  id: string;
  datasetId: string;
  datasetName: string;
  version: string;
  stage: string;
  kafkaStatus: string;
  sparkStatus: string;
  duration: string;
  topic: string;
  createdAt: string;
}

export interface ClusterHealth {
  kafkaStatus: string;
  sparkStatus: string;
  storageStatus: string;
  storageBucket: string;
  storageEndpoint: string;
}
